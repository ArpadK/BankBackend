package arpad.bank.bankbackend.handlers;

import arpad.bank.bankbackend.dbmodel.MutatieStatus;
import arpad.bank.bankbackend.dbmodel.Rekening;
import arpad.bank.bankbackend.dbmodel.RekeningMutatie;
import arpad.bank.bankbackend.integration.eventstore.eventstoreDTOs.TransferEvent;
import arpad.bank.bankbackend.repository.RekeningMutatieRepository;
import arpad.bank.bankbackend.repository.RekeningRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class TransferEventHandler {

	private RekeningRepository rekeningRepository;
	private RekeningMutatieRepository rekeningMutatieRepository;

	/**
	 * Register the contents of a transferEvent to the database.
	 * @param transferEvent
	 */
	public void handleNewTransferEvent(TransferEvent transferEvent){
		switch (transferEvent.getTransferEventType()){
			case CREATED: createTransaction(transferEvent); break;
			case CANCELED: rollbackTransaction(transferEvent); break;
			case COMPLETED: finalizeTransaction(transferEvent); break;
		}
	}

	/**
	 * Storing a new transfer in the database after a transfer event.
	 * @param transferEvent Of new transfer
	 */
	private void createTransaction(TransferEvent transferEvent){
		log.info("Storing new Transfer {"+ transferEvent.getTransferNumber() +"} in the database");
		Rekening rekening = rekeningRepository.getRekeningByRekeningNummer(transferEvent.getRekeningNummer());

		rekening.updateSaldo(transferEvent.getTypeOfMutatie(), transferEvent.getAmount());

		RekeningMutatie newRekeningMutatie = new RekeningMutatie(transferEvent.getTransferNumber(), transferEvent.getTegenRekeningNummer(), transferEvent.getTypeOfMutatie(), transferEvent.getAmount(), rekening, MutatieStatus.Pending);
		rekening.addRekeningMutatie(newRekeningMutatie);

		rekeningMutatieRepository.save(newRekeningMutatie);
		rekeningRepository.save(rekening);
		rekeningRepository.flush();
	}

	/**
	 * After the money was unsuccessfully transferred to of from the tegenrekening. Update the rekeningMutatie from pending to Canceled
	 * @param transferEvent Of which the transaction should be rollbacked
	 */
	private void rollbackTransaction(TransferEvent transferEvent){
		log.info("Rolling back Transfer {"+ transferEvent.getTransferNumber() +"} and storing it in the database");
		RekeningMutatie rekeningMutatie = rekeningMutatieRepository.getRekeningMutatieByTransferNumberAndRekeningNummer(transferEvent.getTransferNumber(), transferEvent.getRekeningNummer());

		Rekening rekening = rekeningMutatie.getRekening();
		rekening.updateSaldo(rekeningMutatie.getTypeOfMutatie().inverted(), rekeningMutatie.getAmount());

		rekeningMutatie.cancelMutation();
		rekeningRepository.save(rekening);
	}

	/**
	 * After the money was successfully transfered to of from the tegenrekening. Update the rekeningMutatie from pending to Completed
	 * @param transferEvent Of which the transaction should be finalized
	 */
	private void finalizeTransaction(TransferEvent transferEvent){
		log.info("Finalize Transfer {"+ transferEvent.getTransferNumber() +"} and storing it in the database");
		RekeningMutatie rekeningMutatie = rekeningMutatieRepository.getRekeningMutatieByTransferNumberAndRekeningNummer(transferEvent.getTransferNumber(), transferEvent.getRekeningNummer());

		rekeningMutatie.finalizeMutation();
		rekeningMutatieRepository.save(rekeningMutatie);
	}
}
