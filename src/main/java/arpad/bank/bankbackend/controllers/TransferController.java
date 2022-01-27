package arpad.bank.bankbackend.controllers;

import arpad.bank.bankbackend.controllers.apiDTOs.TransferRequest;
import arpad.bank.bankbackend.controllers.apiDTOs.TransferResponse;
import arpad.bank.bankbackend.dbmodel.TypeOfMutatie;
import arpad.bank.bankbackend.exceptions.ExternalCommunicationException;
import arpad.bank.bankbackend.exceptions.TransferIllegalException;
import arpad.bank.bankbackend.handlers.NewTransferHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TransferController {

	private NewTransferHandler newTransferHandler;

	public TransferController(NewTransferHandler newTransferHandler){
		this.newTransferHandler = newTransferHandler;
	}

	@PostMapping("/newTransfer")
	public TransferResponse newTransfer(@RequestBody TransferRequest transferRequest) {
		log.info("Received transfer request from " + transferRequest.getFromRekeningNummer() + " to " + transferRequest.getToRekeningNummer());
		// TODO: Input validation
		// TODO: Check if fromRekening is Owned by signed in user

		TransferResponse transferResponse = new TransferResponse();

		// TODO: change input so a mutatie can also be BIJ
		// TODO: add proper http response codes
		try {
			newTransferHandler.handleNewTransfer(transferRequest.getFromRekeningNummer(), transferRequest.getToRekeningNummer(), transferRequest.getAmount(), TypeOfMutatie.AF);
		} catch (TransferIllegalException e) {
			transferResponse.setTransferSuccessful(false);
			transferResponse.setTransferNotSuccessfulReason(e.getMessage());
			return transferResponse;
		} catch (ExternalCommunicationException e){
			transferResponse.setTransferSuccessful(false);
			transferResponse.setTransferNotSuccessfulReason("An internal error occured. Please try again later");
			return transferResponse;
		}
		transferResponse.setTransferSuccessful(true);
		return transferResponse;
	}
}
