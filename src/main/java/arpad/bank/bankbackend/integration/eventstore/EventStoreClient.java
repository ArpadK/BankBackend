package arpad.bank.bankbackend.integration.eventstore;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventStoreClient {

	public void registerNewTransferEvent(){
		log.warn("no eventstore configured yet, can't register this NewTransferEvent");
	}

	public void registerTransferCompletedEvent(){
		log.warn("no eventstore configured yet, can't register this TransferCompletedEvent");
	}

	public void registerTransferCancelledEvent(){
		log.warn("no eventstore configured yet, can't register this TransferCancelledEvent");
	}
}
