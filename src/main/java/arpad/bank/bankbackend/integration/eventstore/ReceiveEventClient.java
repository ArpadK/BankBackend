package arpad.bank.bankbackend.integration.eventstore;

import arpad.bank.bankbackend.handlers.TransferEventHandler;
import arpad.bank.bankbackend.integration.eventstore.eventstoreDTOs.TransferEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Controller;

import javax.transaction.Transactional;

@Controller
@Slf4j
public class ReceiveEventClient {

	private RabbitTemplate template;
	private TransferEventHandler transferEventHandler;

	public ReceiveEventClient(RabbitTemplate rabbitTemplate, TransferEventHandler newTransferHandler){
		this.template = rabbitTemplate;
		this.transferEventHandler = newTransferHandler;
	}

	@Transactional
	@RabbitListener(queues = "${bank.transfer.queue.name}")
	public void receiveNewTransferEvent(TransferEvent transferEvent){
		log.info("TransferCreatedEvent received. Updating model");
		try{
			transferEventHandler.handleNewTransferEvent(transferEvent);
		}catch(Exception e){
			log.error("could not process event", e);
		}
	}
}
