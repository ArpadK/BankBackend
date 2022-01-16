package arpad.bank.bankbackend.handlers;

import arpad.bank.bankbackend.dbmodel.MutatieStatus;
import arpad.bank.bankbackend.dbmodel.Rekening;
import arpad.bank.bankbackend.dbmodel.RekeningMutatie;
import arpad.bank.bankbackend.dbmodel.TypeOfMutatie;
import arpad.bank.bankbackend.helpers.RekeningNummerHelper;
import arpad.bank.bankbackend.integration.eventstore.EventStoreClient;
import arpad.bank.bankbackend.integration.external.exchange.TransferRabbitMQClient;
import arpad.bank.bankbackend.integration.external.exchange.externalExchangeDTOs.TransferRequest;
import arpad.bank.bankbackend.repository.RekeningMutatieRepository;
import arpad.bank.bankbackend.repository.RekeningRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
@AllArgsConstructor
public class NewTransferHandler {

	private RekeningRepository rekeningRepository;
	private RekeningMutatieRepository rekeningMutatieRepository;
	private RekeningNummerHelper rekeningNummerHelper;
	private IncommingTransferHandler incommingTransferHandler;
	private EventStoreClient eventStoreClient;

	/**
	 * start a new transfer
	 * @param rekeningNummer the rekeningnummer you want to transfer from/to
	 * @param tegenRekeningNummer the rekeningnummer you want to transfer to/from
	 * @param amount the amount you want to transfer
	 * @param typeOfMutatie indicate if you want to add or remove money from the rekeningNummer
	 * @return a boolean indicating if the transaction was successful
	 */
	public boolean handleNewTransfer(String rekeningNummer, String tegenRekeningNummer, BigDecimal amount, TypeOfMutatie typeOfMutatie){
		boolean isInternalTransaction = rekeningNummerHelper.isBankRekeningInternalRekening(tegenRekeningNummer);

		// get the Rekening that is starting this transfer from the database
		Rekening rekening = rekeningRepository.getRekeningByRekeningNummer(rekeningNummer);
		// check if Tranfer is legal
		if(!rekening.checkIfTransferIsLegal(true, tegenRekeningNummer, amount, typeOfMutatie, rekeningRepository)){
			return false;
		}

		rekening.updateSaldo(typeOfMutatie, amount);

		RekeningMutatie newRekeningMutatie = new RekeningMutatie(tegenRekeningNummer, typeOfMutatie,amount, rekening, MutatieStatus.Pending);
		rekening.addRekeningMutatie(newRekeningMutatie);

		eventStoreClient.registerNewTransferEvent();
		rekeningMutatieRepository.save(newRekeningMutatie);
		rekeningRepository.save(rekening);
		rekeningRepository.flush();

		log.info("Checking if it is an Internal transfer");
		String transferNumber = rekeningNummerHelper.mapRekeningMutatieIdToTransferNumber(newRekeningMutatie.getId());
		boolean transferResult;
		if(isInternalTransaction){
			log.info("Transfering money to an internal bankaccount");
			transferResult = handleInternalTransfer(rekeningNummer, tegenRekeningNummer, amount, typeOfMutatie, transferNumber);
		}else{
			log.info("Transfering money to an external bankaccount");
			transferResult = handleExternalTransfer(transferNumber);
		}

		return transferResult;
	}

	/**
	 * a transaction to another bank is handled asynchronously. This methods handles the responce from the other bank
	 * @param transferNumber The transferNumber
	 * @param transferSuccessful Indicate if the transaction was successful
	 */
	public void handleExternalTransferResponse(String transferNumber, boolean transferSuccessful){
		String rekeningMutatieId = transferNumber;
		//TODO: null check on rekeningMutatie
		RekeningMutatie rekeningMutatie = rekeningMutatieRepository.findById(rekeningNummerHelper.mapTransferNumberToRekeningMutatieId(transferNumber)).get();
		if(transferSuccessful){
			eventStoreClient.registerTransferCompletedEvent();
			rekeningMutatie.finalizeMutation();
			rekeningMutatieRepository.save(rekeningMutatie);
		}else{
			eventStoreClient.registerTransferCancelledEvent();
			rekeningMutatie.cancelMutation();
			rekeningMutatieRepository.save(rekeningMutatie);
		}
	}

	/**
	 * Handle an internal transaction. This will be done synchronously.
	 * @param rekeningnummer the rekeningnummer you want to transfer from/to
	 * @param tegenRekeningNummer the rekeningnummer you want to transfer to/from
	 * @param amount the amount you want to transfer
	 * @param typeOfMutatie indicate if you want to add or remove money from the rekeningNummer
	 * @param transferNumber the transferNumber of this transaction
	 * @return a boolean indicating if the transaction is successful
	 */
	//TODO: Implement some sort of locking
	private boolean handleInternalTransfer(String rekeningnummer, String tegenRekeningNummer, BigDecimal amount, TypeOfMutatie typeOfMutatie, String transferNumber){
		log.info("Since this is an internal transaction, transfer money synchronously");
		boolean transferSuccessful = incommingTransferHandler.handleIncomingTransfer(tegenRekeningNummer, rekeningnummer, amount, typeOfMutatie.inverted());

		RekeningMutatie rekeningMutatie = rekeningMutatieRepository.findById(rekeningNummerHelper.mapTransferNumberToRekeningMutatieId(transferNumber)).get();

		if(transferSuccessful){
			eventStoreClient.registerTransferCompletedEvent();
			log.info("Internal transfer successful, Regestering transaction");
			finalizeTransaction(rekeningMutatie);
			return true;
		}else{
			eventStoreClient.registerTransferCancelledEvent();
			log.info("Internal transfer unsuccessful, rollbacking transaction");
			rollbackTransaction(rekeningMutatie);
			return false;
		}
	}

	/**
	 * Handle an internal transaction. This will be done asynchronously.
	 * @param transferNumber The transferNumber of this transaction
	 * @return A boolean indicating if the transfer was successfully posted on the interBank exchange
	 */
	private boolean handleExternalTransfer(String transferNumber){
		log.info("Since this is an external transaction, transfer money asynchronously");
		TransferRabbitMQClient transferRabbitMQClient = new TransferRabbitMQClient();
		TransferRequest transferRequest = new TransferRequest();
		boolean transactionSuccessfullyPostedOnExchange = transferRabbitMQClient.publishNewTransferOnExchange(transferRequest);

		if(transactionSuccessfullyPostedOnExchange){
			log.info("Transaction successfully send to external bank");
		}else{
			log.info("Transaction could not be send to the external bank, cancelling transaction");
			RekeningMutatie rekeningMutatie = rekeningMutatieRepository.findById(rekeningNummerHelper.mapTransferNumberToRekeningMutatieId(transferNumber)).get();
			rollbackTransaction(rekeningMutatie);
		}

		return transactionSuccessfullyPostedOnExchange;
	}

	/**
	 * After the money was successfully transfered to of from the tegenrekening. Update the rekeningMutatie from pending to Completed
	 * @param rekeningMutatie The rekeningMutatie to update
	 */
	private void finalizeTransaction(RekeningMutatie rekeningMutatie){
		// subtract amount from saldo and save to db
		rekeningMutatie.finalizeMutation();
		rekeningMutatieRepository.save(rekeningMutatie);
	}

	/**
	 * After the money was unsuccessfully transfered to of from the tegenrekening. Update the rekeningMutatie from pending to Canceled
	 * @param rekeningMutatie The rekeningMutatie to update
	 */
	//TODO: Implement some sort of locking
	private void rollbackTransaction(RekeningMutatie rekeningMutatie){
		Rekening rekening = rekeningMutatie.getRekening();
		rekening.updateSaldo(rekeningMutatie.getTypeOfMutatie().inverted(), rekeningMutatie.getAmount());

		rekeningMutatie.cancelMutation();
		rekeningRepository.save(rekening);
	}
}
