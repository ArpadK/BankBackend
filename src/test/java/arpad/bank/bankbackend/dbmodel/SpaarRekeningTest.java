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
public class SpaarRekeningTest {
	@Autowired
	RekeningRepository rekeningRepository;

	@Autowired
	KlantRepository klantRepository;

	SpaarRekening spaarRekeningParticulier;
	SpaarRekening spaarRekeningZakelijk;
	DoorlopendeRekening eigenTegenRekeningParticulier;
	DoorlopendeRekening eigenTegenRekeningZaklelijk;
	DoorlopendeRekening andereTegenRekening;

	@BeforeAll
	public void setupSpaarRekeningen(){
		BigDecimal saldo = new BigDecimal(500);

		Particulier particuliereKlant = new Particulier();
		Zakelijk zakelijkeKlant = new Zakelijk();
		Particulier andereKlant = new Particulier();

		klantRepository.save(particuliereKlant);
		klantRepository.save(zakelijkeKlant);
		klantRepository.save(andereKlant);

		spaarRekeningParticulier = new SpaarRekening();
		spaarRekeningParticulier.setSaldo(saldo);
		spaarRekeningParticulier.setKlant(particuliereKlant);
		spaarRekeningParticulier.setRekeningnummer("spaarRekeningParticulierNummer");

		spaarRekeningZakelijk = new SpaarRekening();
		spaarRekeningZakelijk.setSaldo(saldo);
		spaarRekeningZakelijk.setKlant(zakelijkeKlant);
		spaarRekeningZakelijk.setRekeningnummer("spaarRekeningZakelijkNummer");

		eigenTegenRekeningParticulier = new DoorlopendeRekening();
		eigenTegenRekeningParticulier.setSaldo(new BigDecimal(0));
		eigenTegenRekeningParticulier.setKlant(particuliereKlant);
		eigenTegenRekeningParticulier.setRekeningnummer("TegenRekeningNummerParticulier");

		eigenTegenRekeningZaklelijk = new DoorlopendeRekening();
		eigenTegenRekeningZaklelijk.setSaldo(new BigDecimal(0));
		eigenTegenRekeningZaklelijk.setKlant(zakelijkeKlant);
		eigenTegenRekeningZaklelijk.setRekeningnummer("TegenRekeningNummerParticulier");

		andereTegenRekening = new DoorlopendeRekening();
		andereTegenRekening.setSaldo(new BigDecimal(0));
		andereTegenRekening.setKlant(andereKlant);
		andereTegenRekening.setRekeningnummer("TegenRekeningNummerAndereKlant");

		rekeningRepository.save(spaarRekeningParticulier);
		rekeningRepository.save(spaarRekeningZakelijk);
		rekeningRepository.save(eigenTegenRekeningParticulier);
		rekeningRepository.save(eigenTegenRekeningZaklelijk);
		rekeningRepository.save(andereTegenRekening);
	}

	@Test
	public void checkIfTransferIsLegel_afTransferWithEnoughSaldoOnParticulier_shouldReturnTrue(){
		// Arrange
		BigDecimal transferAmount = new BigDecimal(499);
		boolean internaltransfer = true;
		TypeOfMutatie typeOfMutatie = TypeOfMutatie.AF;

		// Act
		boolean result = spaarRekeningParticulier.checkIfTransferIsLegal(internaltransfer, eigenTegenRekeningParticulier.getRekeningnummer(), transferAmount, typeOfMutatie, rekeningRepository);

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
		boolean result = spaarRekeningParticulier.checkIfTransferIsLegal(internaltransfer, eigenTegenRekeningParticulier.getRekeningnummer(), transferAmount, typeOfMutatie, rekeningRepository);

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
		boolean result = spaarRekeningParticulier.checkIfTransferIsLegal(internaltransfer, eigenTegenRekeningParticulier.getRekeningnummer(), transferAmount, typeOfMutatie, rekeningRepository);

		// Assert
		assertThat(result).isFalse();
	}

	@Test
	public void checkIfTransferIsLegel_afTransferWithEnoughSaldoOnZakelijk_shouldReturnTrue(){
		// Arrange
		BigDecimal transferAmount = new BigDecimal(499);
		boolean internaltransfer = true;
		TypeOfMutatie typeOfMutatie = TypeOfMutatie.AF;

		// Act
		boolean result = spaarRekeningZakelijk.checkIfTransferIsLegal(internaltransfer, eigenTegenRekeningZaklelijk.getRekeningnummer(), transferAmount, typeOfMutatie, rekeningRepository);

		// Assert
		assertThat(result).isTrue();
	}

	@Test
	public void checkIfTransferIsLegel_afTransferWithJustEnoughSaldoOnZakelijk_shouldReturnTrue(){
		// Arrange
		BigDecimal transferAmount = new BigDecimal(500);
		boolean internaltransfer = true;
		TypeOfMutatie typeOfMutatie = TypeOfMutatie.AF;

		// Act
		boolean result = spaarRekeningZakelijk.checkIfTransferIsLegal(internaltransfer, eigenTegenRekeningZaklelijk.getRekeningnummer(), transferAmount, typeOfMutatie, rekeningRepository);

		// Assert
		assertThat(result).isTrue();
	}

	@Test
	public void checkIfTransferIsLegel_afTransferWithNotEnoughSaldoOnZakelijk_shouldReturnFalse(){
		// Arrange
		BigDecimal transferAmount = new BigDecimal(501);
		boolean internaltransfer = true;
		TypeOfMutatie typeOfMutatie = TypeOfMutatie.AF;

		// Act
		boolean result = spaarRekeningZakelijk.checkIfTransferIsLegal(internaltransfer, eigenTegenRekeningZaklelijk.getRekeningnummer(), transferAmount, typeOfMutatie, rekeningRepository);

		// Assert
		assertThat(result).isFalse();
	}

	@Test
	public void checkIfTransferIsLegel_afParticulierWithATegenrekenenOwnedBySomeoneElse_shouldReturnFalse(){
		// Arrange
		BigDecimal transferAmount = new BigDecimal(500);
		boolean internaltransfer = true;
		TypeOfMutatie typeOfMutatie = TypeOfMutatie.AF;

		// Act
		boolean result = spaarRekeningParticulier.checkIfTransferIsLegal(internaltransfer, andereTegenRekening.getRekeningnummer(), transferAmount, typeOfMutatie, rekeningRepository);

		// Assert
		assertThat(result).isFalse();
	}

	@Test
	public void checkIfTransferIsLegel_afZakelijkWithATegenrekenenOwnedBySomeoneElse_shouldReturnFalse(){
		// Arrange
		BigDecimal transferAmount = new BigDecimal(500);
		boolean internaltransfer = true;
		TypeOfMutatie typeOfMutatie = TypeOfMutatie.AF;

		// Act
		boolean result = spaarRekeningZakelijk.checkIfTransferIsLegal(internaltransfer, andereTegenRekening.getRekeningnummer(), transferAmount, typeOfMutatie, rekeningRepository);

		// Assert
		assertThat(result).isFalse();
	}

	@Test
	public void checkIfTransferIsLegel_BijParticulierWithOwnTegenrekeneing_shouldReturnTrue(){
		// Arrange
		BigDecimal transferAmount = new BigDecimal(500);
		boolean internaltransfer = true;
		TypeOfMutatie typeOfMutatie = TypeOfMutatie.BIJ;

		// Act
		boolean result = spaarRekeningParticulier.checkIfTransferIsLegal(internaltransfer, eigenTegenRekeningParticulier.getRekeningnummer(), transferAmount, typeOfMutatie, rekeningRepository);

		// Assert
		assertThat(result).isTrue();
	}

	@Test
	public void checkIfTransferIsLegel_BijZakelijkWithOwnTegenrekeneing_shouldReturnTrue(){
		// Arrange
		BigDecimal transferAmount = new BigDecimal(500);
		boolean internaltransfer = true;
		TypeOfMutatie typeOfMutatie = TypeOfMutatie.BIJ;

		// Act
		boolean result = spaarRekeningZakelijk.checkIfTransferIsLegal(internaltransfer, eigenTegenRekeningZaklelijk.getRekeningnummer(), transferAmount, typeOfMutatie, rekeningRepository);

		// Assert
		assertThat(result).isTrue();
	}

	@Test
	public void checkIfTransferIsLegel_BijParticulierWithATegenrekenenOwnedBySomeoneElse_shouldReturnFalse(){
		// Arrange
		BigDecimal transferAmount = new BigDecimal(500);
		boolean internaltransfer = true;
		TypeOfMutatie typeOfMutatie = TypeOfMutatie.BIJ;

		// Act
		boolean result = spaarRekeningParticulier.checkIfTransferIsLegal(internaltransfer, andereTegenRekening.getRekeningnummer(), transferAmount, typeOfMutatie, rekeningRepository);

		// Assert
		assertThat(result).isFalse();
	}

	@Test
	public void checkIfTransferIsLegel_BijZakelijkWithATegenrekenenOwnedBySomeoneElse_shouldReturnFalse(){
		// Arrange
		BigDecimal transferAmount = new BigDecimal(500);
		boolean internaltransfer = true;
		TypeOfMutatie typeOfMutatie = TypeOfMutatie.BIJ;

		// Act
		boolean result = spaarRekeningZakelijk.checkIfTransferIsLegal(internaltransfer, andereTegenRekening.getRekeningnummer(), transferAmount, typeOfMutatie, rekeningRepository);

		// Assert
		assertThat(result).isFalse();
	}
}
