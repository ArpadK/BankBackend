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

	private NewTransferHandler newTransferHandler;

	public TransferController(NewTransferHandler newTransferHandler){
		this.newTransferHandler = newTransferHandler;
	}

	@PostMapping("/newTransfer")
	public String newTransfer(@RequestBody TransferRequest transferRequest) {
		log.info("Received transfer request from " + transferRequest.getFromRekeningNummer() + " to " + transferRequest.getToRekeningNummer());
		// TODO: Input validation
		// TODO: Check if fromRekening is Owned by signed in user
		boolean transferSuccessful = newTransferHandler.handleNewTransfer(transferRequest.getFromRekeningNummer(), transferRequest.getToRekeningNummer(), transferRequest.getAmount(), TypeOfMutatie.AF);
		// TODO: Return Succes or Failure

		if(transferSuccessful){
			return "The transfer was successfully submitted";
		}else{
			return "Could not transfer the money. Please contact your bank";
		}
	}
}
