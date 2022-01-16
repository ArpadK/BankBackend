package arpad.bank.bankbackend.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RekeningNummerHelperTest {

	@Autowired
	RekeningNummerHelper rekeningNummerHelper;

	@Value("${bank.name}")
	String bankname;

	@Test
	public void isBankRekeningInternalRekening_withSameBankRekening_shouldReturnTrue(){
		// Arrange
		String testRekening = "NL00"+bankname+"0000000000";

		// Act
		boolean result = rekeningNummerHelper.isBankRekeningInternalRekening(testRekening);

		// Assert
		assertThat(result).isTrue();
	}

	@Test
	public void isBankRekeningInternalRekening_withDifferentBankRekening_shouldReturnFalse(){
		// Arrange
		String testRekening = "NL00AAAA0000000000";

		// Act
		boolean result = rekeningNummerHelper.isBankRekeningInternalRekening(testRekening);

		// Assert
		assertThat(result).isFalse();
	}

	//TODO: write tests for mapping of TransferNumber once that is worked out.
}
