package arpad.bank.bankbackend;

import arpad.bank.bankbackend.dbmodel.Particulier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.time.LocalDate;

@SpringBootApplication
public class BankBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankBackendApplication.class, args);
	}

}
