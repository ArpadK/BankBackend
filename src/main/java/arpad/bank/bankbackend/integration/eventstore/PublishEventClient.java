package arpad.bank.bankbackend.integration.eventstore;

import arpad.bank.bankbackend.RabbitConfig;
import arpad.bank.bankbackend.handlers.NewTransferHandler;
import arpad.bank.bankbackend.integration.eventstore.eventstoreDTOs.TransferCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PublishEventClient {

	private RabbitTemplate template;

	public PublishEventClient(RabbitTemplate rabbitTemplate){
		this.template = rabbitTemplate;
	}

	public void registerNewTransferEvent(TransferCreatedEvent transferCreatedEvent){
		template.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.TRANSFER_EVENT_ROUTING_KEY, transferCreatedEvent);
	}

	public void registerTransferCompletedEvent(){
		log.warn("no eventstore configured yet, can't register this TransferCompletedEvent");
	}

	public void registerTransferCancelledEvent(){
		log.warn("no eventstore configured yet, can't register this TransferCancelledEvent");
	}
}
