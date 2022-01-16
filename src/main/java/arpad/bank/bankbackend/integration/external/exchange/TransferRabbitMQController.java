package arpad.bank.bankbackend.integration.external.exchange;

import arpad.bank.bankbackend.handlers.NewTransferHandler;
import arpad.bank.bankbackend.integration.external.exchange.externalExchangeDTOs.TransferResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransferRabbitMQController {

	private NewTransferHandler newTransferHandler;

	public TransferRabbitMQController(NewTransferHandler newTransferHandler){
		this.newTransferHandler = newTransferHandler;
	}

	public void processTranferResponse(TransferResponse transferResponse){
		log.info("received a transferResponce for transaction " + transferResponse.getTransferNumber());
		newTransferHandler.handleExternalTransferResponse(transferResponse.getTransferNumber(), transferResponse.isTransferSuccessful());
	}
}
