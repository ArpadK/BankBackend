package arpad.bank.bankbackend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoadStartupData {

	@EventListener(ApplicationReadyEvent.class)
	// TODO: Setup queue's to receive new events then replay old events from eventstore before processing new events or requests
	public void replayEventsAtStartup() {
		log.warn("Replaying is not yet configured. NO DATA WILL BE RESTORED");
	}
}
