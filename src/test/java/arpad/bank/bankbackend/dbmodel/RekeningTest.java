package arpad.bank.bankbackend.dbmodel;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RekeningTest {

	@Test
	public void updateSaldo_ShouldAddSaldo_WhenTypeOfMutatieIsBij(){
		// Arrange
		Rekening rekening = new DoorlopendeRekening();
		rekening.saldo = new BigDecimal(100);

		// Act
		rekening.updateSaldo(TypeOfMutatie.BIJ, new BigDecimal(100));

		// Assert
		assertThat(rekening.saldo).isEqualTo(new BigDecimal(200));
	}

	@Test
	public void updateSaldo_ShouldSubtractSaldo_WhenTypeOfMutatieIsAf(){
		// Arrange
		Rekening rekening = new DoorlopendeRekening();
		rekening.saldo = new BigDecimal(100);

		// Act
		rekening.updateSaldo(TypeOfMutatie.AF, new BigDecimal(100));

		// Assert
		assertThat(rekening.saldo).isEqualTo(new BigDecimal(0));
	}
}
