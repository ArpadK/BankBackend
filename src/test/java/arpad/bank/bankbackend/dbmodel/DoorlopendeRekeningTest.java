package arpad.bank.bankbackend.dbmodel;

import arpad.bank.bankbackend.repository.KlantRepository;
import arpad.bank.bankbackend.repository.RekeningRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DoorlopendeRekeningTest {

	@Autowired
	RekeningRepository rekeningRepository;

	@Autowired
	KlantRepository klantRepository;

	DoorlopendeRekening doorlopendeRekeningParticulier;
	DoorlopendeRekening doorlopendeRekeningZakelijk;
	DoorlopendeRekening tegenRekening;

	@BeforeAll
	public void setupDoorlopendeRekeningen(){
		BigDecimal saldo = new BigDecimal(500);

		Particulier particuliereKlant = new Particulier();
		Zakelijk zakelijkeKlant = new Zakelijk();
		Particulier particuliereTegenRekeningKlant = new Particulier();

		klantRepository.save(particuliereKlant);
		klantRepository.save(zakelijkeKlant);
		klantRepository.save(particuliereTegenRekeningKlant);

		doorlopendeRekeningParticulier = new DoorlopendeRekening();
		doorlopendeRekeningParticulier.setSaldo(saldo);
		doorlopendeRekeningParticulier.setKlant(particuliereKlant);
		doorlopendeRekeningParticulier.setRekeningnummer("doorlopendeRekeningParticulierNummer");

		doorlopendeRekeningZakelijk = new DoorlopendeRekening();
		doorlopendeRekeningZakelijk.setSaldo(saldo);
		doorlopendeRekeningZakelijk.setKlant(zakelijkeKlant);
		doorlopendeRekeningZakelijk.setRekeningnummer("doorlopendeRekeningZakelijkNummer");

		tegenRekening = new DoorlopendeRekening();
		tegenRekening.setSaldo(new BigDecimal(0));
		tegenRekening.setKlant(particuliereTegenRekeningKlant);
		tegenRekening.setRekeningnummer("TegenRekeningNummer");

		rekeningRepository.save(doorlopendeRekeningParticulier);
		rekeningRepository.save(doorlopendeRekeningZakelijk);
		rekeningRepository.save(tegenRekening);
	}


	@Test
	public void checkIfTransferIsLegel_afTransferWithEnoughSaldoOnParticulier_shouldReturnTrue(){
		// Arrange
		BigDecimal transferAmount = new BigDecimal(499);
		boolean internaltransfer = true;
		TypeOfMutatie typeOfMutatie = TypeOfMutatie.AF;

		// Act
		boolean result = doorlopendeRekeningParticulier.checkIfTransferIsLegal(internaltransfer, tegenRekening.getRekeningnummer(), transferAmount, typeOfMutatie, rekeningRepository);

		// Assert
		assertThat(result).isTrue();
	}

	@Test
	public void checkIfTransferIsLegel_afTransferWithJustEnoughSaldoOnParticulier_shouldReturnTrue(){
		// Arrange
		BigDecimal transferAmount = new BigDecimal(500);
		boolean internaltransfer = true;
		TypeOfMutatie typeOfMutatie = TypeOfMutatie.AF;

		// Act
		boolean result = doorlopendeRekeningParticulier.checkIfTransferIsLegal(internaltransfer, tegenRekening.getRekeningnummer(), transferAmount, typeOfMutatie, rekeningRepository);

		// Assert
		assertThat(result).isTrue();
	}

	@Test
	public void checkIfTransferIsLegel_afTransferWithNotEnoughSaldoOnParticulier_shouldReturnFalse(){
		// Arrange
		BigDecimal transferAmount = new BigDecimal(501);
		boolean internaltransfer = true;
		TypeOfMutatie typeOfMutatie = TypeOfMutatie.AF;

		// Act
		boolean result = doorlopendeRekeningParticulier.checkIfTransferIsLegal(internaltransfer, tegenRekening.getRekeningnummer(), transferAmount, typeOfMutatie, rekeningRepository);

		// Assert
		assertThat(result).isFalse();
	}

	@Test
	public void checkIfTransferIsLegel_afTransferWithEnoughSaldoOnZakelijk_shouldReturnTrue(){
		// Arrange
		BigDecimal transferAmount = new BigDecimal(5499);
		boolean internaltransfer = true;
		TypeOfMutatie typeOfMutatie = TypeOfMutatie.AF;

		// Act
		boolean result = doorlopendeRekeningZakelijk.checkIfTransferIsLegal(internaltransfer, tegenRekening.getRekeningnummer(), transferAmount, typeOfMutatie, rekeningRepository);

		// Assert
		assertThat(result).isTrue();
	}

	@Test
	public void checkIfTransferIsLegel_afTransferWithJustEnoughSaldoOnZakelijk_shouldReturnTrue(){
		// Arrange
		BigDecimal transferAmount = new BigDecimal(5500);
		boolean internaltransfer = true;
		TypeOfMutatie typeOfMutatie = TypeOfMutatie.AF;

		// Act
		boolean result = doorlopendeRekeningZakelijk.checkIfTransferIsLegal(internaltransfer, tegenRekening.getRekeningnummer(), transferAmount, typeOfMutatie, rekeningRepository);

		// Assert
		assertThat(result).isTrue();
	}

	@Test
	public void checkIfTransferIsLegel_afTransferWithNotEnoughSaldoOnZakelijk_shouldReturnFalse(){
		// Arrange
		BigDecimal transferAmount = new BigDecimal(5501);
		boolean internaltransfer = true;
		TypeOfMutatie typeOfMutatie = TypeOfMutatie.AF;

		// Act
		boolean result = doorlopendeRekeningZakelijk.checkIfTransferIsLegal(internaltransfer, tegenRekening.getRekeningnummer(), transferAmount, typeOfMutatie, rekeningRepository);

		// Assert
		assertThat(result).isFalse();
	}
}
