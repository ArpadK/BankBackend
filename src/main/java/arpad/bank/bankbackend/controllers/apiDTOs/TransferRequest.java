package arpad.bank.bankbackend.controllers.apiDTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
public class TransferRequest {
	@Getter
	private final String fromRekeningNummer;
	@Getter
	private final String toRekeningNummer;
	@Getter
	private final BigDecimal amount;
}
