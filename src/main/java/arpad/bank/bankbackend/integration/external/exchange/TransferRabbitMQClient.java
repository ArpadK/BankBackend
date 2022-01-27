package arpad.bank.bankbackend.integration.external.exchange;


import arpad.bank.bankbackend.integration.external.exchange.externalExchangeDTOs.TransferRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransferRabbitMQClient {

	public boolean publishNewTransferOnExchange(TransferRequest transferRequest){
		log.warn("No rabbit connection setup yet, message will not be send to other bank!");
		return false;
	}
}
