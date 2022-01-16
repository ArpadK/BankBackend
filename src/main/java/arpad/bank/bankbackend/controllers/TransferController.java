package arpad.bank.bankbackend.controllers;

import arpad.bank.bankbackend.controllers.apiDTOs.TransferRequest;
import arpad.bank.bankbackend.dbmodel.TypeOfMutatie;
import arpad.bank.bankbackend.handlers.NewTransferHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TransferController {

	NewTransferHandler newTransferHandler;

	public TransferController(NewTransferHandler newTransferHandler){
		this.newTransferHandler = newTransferHandler;
	}

	@PostMapping("/newTransfer")
	public void newTransfer(@RequestBody TransferRequest transferRequest) {
		log.info("Received transfer request from " + transferRequest.getFromRekeningNummer() + " to " + transferRequest.getToRekeningNummer());
		// TODO: Input validation
		// TODO: Check if fromRekening is Owned by signed in user
		newTransferHandler.handleNewTransfer(transferRequest.getFromRekeningNummer(), transferRequest.getToRekeningNummer(), transferRequest.getAmount(), TypeOfMutatie.AF);
		// TODO: Return Succes or Failure
	}
}
