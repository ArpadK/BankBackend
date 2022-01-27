package arpad.bank.bankbackend.handlers;

import arpad.bank.bankbackend.dbmodel.MutatieStatus;
import arpad.bank.bankbackend.dbmodel.Rekening;
import arpad.bank.bankbackend.dbmodel.RekeningMutatie;
import arpad.bank.bankbackend.dbmodel.TypeOfMutatie;
import arpad.bank.bankbackend.exceptions.TransferIllegalException;
import arpad.bank.bankbackend.integration.eventstore.PublishEventClient;
import arpad.bank.bankbackend.integration.eventstore.eventstoreDTOs.TransferEvent;
import arpad.bank.bankbackend.integration.eventstore.eventstoreDTOs.TransferEventType;
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
	private PublishEventClient publishEventClient;

	/**
	 * Handles an transfer started by another account from the same bank, or an external bank.
	 *
	 * @param tegenRekeningNummer   The rekeningNummer of the account that started this transfer
	 * @param amount                The amount you want to transfer
	 * @param typeOfMutatie         Specify if you want to add or remove money
	 */
	public void handleIncomingTransfer(String transferNumber, String rekeningNummer, String tegenRekeningNummer, BigDecimal amount, TypeOfMutatie typeOfMutatie) throws TransferIllegalException {
		Rekening rekening = rekeningRepository.getRekeningByRekeningNummer(rekeningNummer);
		rekening.checkIfTransferIsLegal(true, tegenRekeningNummer, amount, typeOfMutatie, rekeningRepository);

		TransferEvent transferEvent = new TransferEvent(
				TransferEventType.CREATED,
				transferNumber,
				rekeningNummer,
				tegenRekeningNummer,
				amount,
				typeOfMutatie
		);

		publishEventClient.registerNewTransferEvent(transferEvent);

		// since this event was checked and verified on both sides, we van immediately finalize the transfer.
		TransferEvent transferCompletedEvent = new TransferEvent(
				TransferEventType.COMPLETED,
				transferNumber,
				rekeningNummer,
				tegenRekeningNummer,
				amount,
				typeOfMutatie
		);

		publishEventClient.registerNewTransferEvent(transferCompletedEvent);
	}
}
