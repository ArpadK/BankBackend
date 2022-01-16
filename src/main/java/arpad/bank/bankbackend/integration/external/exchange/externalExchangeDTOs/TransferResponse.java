package arpad.bank.bankbackend.integration.external.exchange.externalExchangeDTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class TransferResponse {
	@Getter @Setter
	private String transferNumber;
	@Getter @Setter
	private boolean transferSuccessful;
	@Getter @Setter
	private String transferNonSuccessfulReason;
}
