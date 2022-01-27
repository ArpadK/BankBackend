package arpad.bank.bankbackend.helpers;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class RekeningNummerHelper {

	@Value("${bank.name}")
	@Setter
	private String bankname;

	/**
	 * Check if the Rekening belongs to this bank
	 * @param rekening The Rekening you want to check
	 * @return A boolean indicating if the account belongs to this bank
	 */
	public boolean isBankRekeningInternalRekening(String rekening){
		Pattern ibanRegex = Pattern.compile("[a-zA-Z]{2}[0-9]{2}([a-zA-Z0-9]{4})[0-9]{7}([a-zA-Z0-9]?){0,16}");
		Matcher ibanMatcher = ibanRegex.matcher(rekening);
		ibanMatcher.matches();
		String bankCode = ibanMatcher.group(1);
		boolean internalTransfer = bankCode.equals(bankname);
		return internalTransfer;
	}

	/**
	 * Generate a new TransferNumber
	 * @return The transferNumber of the RekeningMutatieId
	 */
	public String generateNewTransferNumber(){
		return bankname + "_" + UUID.randomUUID().toString();
	}
}
