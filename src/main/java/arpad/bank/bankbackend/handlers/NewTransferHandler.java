package arpad.bank.bankbackend.handlers;

import arpad.bank.bankbackend.dbmodel.Rekening;
import arpad.bank.bankbackend.dbmodel.TypeOfMutatie;
import arpad.bank.bankbackend.exceptions.ExternalCommunicationException;
import arpad.bank.bankbackend.exceptions.TransferIllegalException;
import arpad.bank.bankbackend.helpers.RekeningNummerHelper;
import arpad.bank.bankbackend.integration.eventstore.PublishEventClient;
import arpad.bank.bankbackend.integration.eventstore.eventstoreDTOs.TransferEvent;
import arpad.bank.bankbackend.integration.eventstore.eventstoreDTOs.TransferEventType;
import arpad.bank.bankbackend.integration.external.exchange.TransferRabbitMQClient;
import arpad.bank.bankbackend.integration.external.exchange.externalExchangeDTOs.TransferRequest;
import arpad.bank.bankbackend.integration.external.exchange.externalExchangeDTOs.TransferResponse;
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
	private RekeningNummerHelper rekeningNummerHelper;
	private IncommingTransferHandler incommingTransferHandler;
	private PublishEventClient publishEventClient;

	/**
	 * Start a new transfer
	 * @param rekeningNummer the rekeningnummer you want to transfer from/to
	 * @param tegenRekeningNummer the rekeningnummer you want to transfer to/from
	 * @param amount the amount you want to transfer
	 * @param typeOfMutatie indicate if you want to add or remove money from the rekeningNummer
	 */
	public void handleNewTransfer(String rekeningNummer, String tegenRekeningNummer, BigDecimal amount, TypeOfMutatie typeOfMutatie) throws TransferIllegalException, ExternalCommunicationException {
		// get the Rekening that is starting this transfer from the database
		// TODO: Check if bank account exists
		Rekening rekening = rekeningRepository.getRekeningByRekeningNummer(rekeningNummer);

		// check if Tranfer is legal
		rekening.checkIfTransferIsLegal(true, tegenRekeningNummer, amount, typeOfMutatie, rekeningRepository);

		String transferNumber = rekeningNummerHelper.generateNewTransferNumber();

		TransferEvent transferEvent = new TransferEvent(
				TransferEventType.CREATED,
				transferNumber,
				rekeningNummer,
				tegenRekeningNummer,
				amount,
				typeOfMutatie
		);

		publishEventClient.registerNewTransferEvent(transferEvent);

		log.info("Checking if it is an Internal transfer");
		boolean isInternalTransaction = rekeningNummerHelper.isBankRekeningInternalRekening(tegenRekeningNummer);

		if(isInternalTransaction){
			log.info("Transfering money to an internal bankaccount");
			handleInternalTransfer(rekeningNummer, tegenRekeningNummer, amount, typeOfMutatie, transferNumber);
		}else{
			log.info("Transfering money to an external bankaccount");
			handleExternalTransfer(rekeningNummer, tegenRekeningNummer, amount, typeOfMutatie, transferNumber);
		}
	}

	/**
	 * a transaction to another bank is handled asynchronously. This methods handles the response from the other bank
	 * @param transferNumber The transferNumber
	 * @param transferSuccessful Indicate if the transaction was successful
	 */
	public void handleExternalTransferResponse(String transferNumber, String rekeningnummer, String tegenRekeningNummer, BigDecimal amount, TypeOfMutatie typeOfMutatie, boolean transferSuccessful){

		TransferEvent transferEvent = new TransferEvent();

		transferEvent.setTransferNumber(transferNumber);
		transferEvent.setRekeningNummer(rekeningnummer);
		transferEvent.setTegenRekeningNummer(tegenRekeningNummer);
		transferEvent.setAmount(amount);
		transferEvent.setTypeOfMutatie(typeOfMutatie);

		if(transferSuccessful){
			transferEvent.setTransferEventType(TransferEventType.COMPLETED);
		}else{
			transferEvent.setTransferEventType(TransferEventType.CANCELED);
		}
		publishEventClient.registerNewTransferEvent(transferEvent);
	}

	/**
	 * Handle an internal transaction. This will be done synchronously.
	 * @param rekeningnummer the rekeningnummer you want to transfer from/to
	 * @param tegenRekeningNummer the rekeningnummer you want to transfer to/from
	 * @param amount the amount you want to transfer
	 * @param typeOfMutatie indicate if you want to add or remove money from the rekeningNummer
	 * @param transferNumber the transferNumber of this transaction
	 */
	private void handleInternalTransfer(String rekeningnummer, String tegenRekeningNummer, BigDecimal amount, TypeOfMutatie typeOfMutatie, String transferNumber) throws TransferIllegalException {
		log.info("Since this is an internal transaction, transfer money synchronously");

		TransferEvent transferEvent = new TransferEvent();

		transferEvent.setTransferNumber(transferNumber);
		transferEvent.setRekeningNummer(rekeningnummer);
		transferEvent.setTegenRekeningNummer(tegenRekeningNummer);
		transferEvent.setAmount(amount);
		transferEvent.setTypeOfMutatie(typeOfMutatie);

		try{
			incommingTransferHandler.handleIncomingTransfer(transferNumber, tegenRekeningNummer, rekeningnummer, amount, typeOfMutatie.inverted());

			transferEvent.setTransferEventType(TransferEventType.COMPLETED);
			publishEventClient.registerNewTransferEvent(transferEvent);
		}catch(TransferIllegalException e){
			transferEvent.setTransferEventType(TransferEventType.CANCELED);
			publishEventClient.registerNewTransferEvent(transferEvent);

			// Create a new Exception in order to not expose information of another klant's bankrekening
			throw new TransferIllegalException("The tegenrekening has rejected the transfer");
		}
	}

	/**
	 * Handle an external transaction. This will be done asynchronously.
	 * @param transferNumber The transferNumber of this transaction
	 */
	private void handleExternalTransfer(String rekeningnummer, String tegenRekeningNummer, BigDecimal amount, TypeOfMutatie typeOfMutatie, String transferNumber) throws ExternalCommunicationException {
		log.info("Since this is an external transaction, transfer money asynchronously");
		TransferRabbitMQClient transferRabbitMQClient = new TransferRabbitMQClient();
		TransferRequest transferRequest = new TransferRequest();
		boolean transactionSuccessfullyPostedOnExchange = transferRabbitMQClient.publishNewTransferOnExchange(transferRequest);

		if(transactionSuccessfullyPostedOnExchange){
			log.info("Transaction successfully send to external bank");
		}else{
			log.info("Transaction could not be send to the external bank, cancelling transaction");
			TransferEvent transferEvent = new TransferEvent(
					TransferEventType.CANCELED,
					transferNumber,
					rekeningnummer,
					tegenRekeningNummer,
					amount,
					typeOfMutatie
			);

			publishEventClient.registerNewTransferEvent(transferEvent);
			throw new ExternalCommunicationException("An error occurred when trying to post a transfer request on the external bank exchange");
		}
	}
}
