package arpad.bank.bankbackend;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class RabbitConfig {

	public static String EXCHANGE;
	public static String TRANSFER_EVENT_QUEUE;
	public static String TRANSFER_EVENT_ROUTING_KEY;

	@Bean
	public Queue transferEventQueue(@Value("${bank.transfer.queue.name}") String name) {
		TRANSFER_EVENT_QUEUE = name;
		return  new Queue(TRANSFER_EVENT_QUEUE, false, true, true);
	}

	@Bean
	public TopicExchange exchange(@Value("${bank.exchange.name}") String name) {
		EXCHANGE = name;
		return new TopicExchange(EXCHANGE);
	}

	@Bean
	public Binding transferBinding(@Qualifier("transferEventQueue") Queue queue, TopicExchange exchange, @Value("${bank.transfer.routing.key}") String routingKey){
		TRANSFER_EVENT_ROUTING_KEY = routingKey;
		return BindingBuilder
				.bind(queue)
				.to(exchange)
				.with(TRANSFER_EVENT_ROUTING_KEY);
	}

	@Bean
	public MessageConverter messageConverter() {
		return  new Jackson2JsonMessageConverter();
	}

	@Bean
	public AmqpTemplate template(ConnectionFactory connectionFactory) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(messageConverter());
		return  template;
	}

}
