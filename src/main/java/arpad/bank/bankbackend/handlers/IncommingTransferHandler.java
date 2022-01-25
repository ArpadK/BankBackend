package arpad.bank.bankbackend.handlers;

import arpad.bank.bankbackend.dbmodel.MutatieStatus;
import arpad.bank.bankbackend.dbmodel.Rekening;
import arpad.bank.bankbackend.dbmodel.RekeningMutatie;
import arpad.bank.bankbackend.dbmodel.TypeOfMutatie;
import arpad.bank.bankbackend.integration.eventstore.EventStoreClient;
import arpad.bank.bankbackend.repository.RekeningMutatieRepository;
import arpad.bank.bankbackend.repository.RekeningRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
@AllArgsConstructor
public class IncommingTransferHandler {

	private RekeningRepository rekeningRepository;
	private RekeningMutatieRepository rekeningMutatieRepository;
	private EventStoreClient eventStoreClient;

	/**
	 * Handles an transfer started by another account from the same bank, or an external bank.
	 *
	 * @param tegenRekeningNummer   The rekeningNummer of the account that started this transfer
	 * @param amount                The amount you want to transfer
	 * @param typeOfMutatie         Specify if you want to add or remove money
	 * @return  a boolean indicating if the transfer was successful
	 */
	public boolean handleIncomingTransfer(String rekeningNummer, String tegenRekeningNummer, BigDecimal amount, TypeOfMutatie typeOfMutatie){
		Rekening rekening = rekeningRepository.getRekeningByRekeningNummer(rekeningNummer);
		if(!rekening.checkIfTransferIsLegal(true, tegenRekeningNummer, amount, typeOfMutatie, rekeningRepository)){
			log.info("Transfer is illegal, canceling transfer");
			return false;
		}
		rekening.updateSaldo(typeOfMutatie, amount);

		RekeningMutatie newRekeningMutatie = new RekeningMutatie(tegenRekeningNummer, typeOfMutatie,amount, rekening, MutatieStatus.Completed);
		rekening.addRekeningMutatie(newRekeningMutatie);

//		eventStoreClient.registerNewTransferEvent();
//		eventStoreClient.registerTransferCompletedEvent();
		rekeningMutatieRepository.save(newRekeningMutatie);
		rekeningRepository.save(rekening);
		rekeningRepository.flush();

		return true;
	}
}
