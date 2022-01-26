package arpad.bank.bankbackend.controllers;

import arpad.bank.bankbackend.dbmodel.*;
import arpad.bank.bankbackend.handlers.NewTransferHandler;
import arpad.bank.bankbackend.integration.external.exchange.TransferRabbitMQController;
import arpad.bank.bankbackend.repository.KlantRepository;
import arpad.bank.bankbackend.repository.RekeningRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@Slf4j
public class SetupMockDataController {

	KlantRepository klantRepository;
	RekeningRepository rekeningRepository;

	NewTransferHandler newTransferHandler;

	public SetupMockDataController(KlantRepository klantRepository, RekeningRepository rekeningRepository, NewTransferHandler newTransferHandler){
		this.klantRepository = klantRepository;
		this.rekeningRepository = rekeningRepository;
		this.newTransferHandler = newTransferHandler;
	}

	@GetMapping("/SetupMockData")
	public String setupMockData() {
		log.warn("Loading mock client data!");

		Particulier particulier1 = new Particulier();
		particulier1.setAdress("Test straat 1");
		particulier1.setBirthDate(LocalDate.of(1997,8,31));
		particulier1.setBSN("Random String");

		Zakelijk zakelijk1 = new Zakelijk();
		zakelijk1.setAdress("Test straat 2");
		zakelijk1.setKVKNummer(111111111);

		klantRepository.save(particulier1);
		klantRepository.save(zakelijk1);

		DoorlopendeRekening doorlopendeRekening1 =  new DoorlopendeRekening();
		doorlopendeRekening1.setRekeningnummer("NL98INGB0003856625");
		doorlopendeRekening1.setSaldo(new BigDecimal(200));
		doorlopendeRekening1.setKlant(particulier1);


		DoorlopendeRekening doorlopendeRekening2 =  new DoorlopendeRekening();
		doorlopendeRekening2.setRekeningnummer("NL98INGB0003856626");
		doorlopendeRekening2.setSaldo(new BigDecimal(200));
		doorlopendeRekening2.setKlant(zakelijk1);

		rekeningRepository.save(doorlopendeRekening1);
		rekeningRepository.save(doorlopendeRekening2);

		return "Client data loaded";
	}

	@GetMapping("/tempRespondToRabbitRequest")
	public void tempRespondToRabbitRequest(){
		TransferRabbitMQController transferRabbitMQController = new TransferRabbitMQController(newTransferHandler);
//		TransferResponse transferResponse = new TransferResponse("1", true, null);
//		transferRabbitMQController.processTranferResponse(transferResponse);
	}
}
