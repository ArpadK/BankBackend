package arpad.bank.bankbackend.integration.eventstore;

import arpad.bank.bankbackend.handlers.NewTransferHandler;
import arpad.bank.bankbackend.integration.eventstore.eventstoreDTOs.TransferCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import javax.transaction.Transactional;

@Controller
@Slf4j
public class ReceiveEventClient {

	private RabbitTemplate template;
	private NewTransferHandler newTransferHandler;

	public ReceiveEventClient(RabbitTemplate rabbitTemplate, NewTransferHandler newTransferHandler){
		this.template = rabbitTemplate;
		this.newTransferHandler = newTransferHandler;
	}

	@Transactional
	@RabbitListener(queues = "${bank.transfer.queue.name}")
	public void receiveNewTransferEvent(TransferCreatedEvent transferCreatedEvent){
		log.info("TransferCreatedEvent received. Updating model");
		try{
			newTransferHandler.handleNewTransferEvent(transferCreatedEvent);
		}catch(Exception e){
			log.error("could not process event", e);
		}
	}
}
