package arpad.bank.bankbackend.integration.eventstore;

import arpad.bank.bankbackend.RabbitConfig;
import arpad.bank.bankbackend.handlers.NewTransferHandler;
import arpad.bank.bankbackend.integration.eventstore.eventstoreDTOs.TransferCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventStoreClient {

	public EventStoreClient(){
	}
}
