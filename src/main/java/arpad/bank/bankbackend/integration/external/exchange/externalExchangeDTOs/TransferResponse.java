package arpad.bank.bankbackend.integration.external.exchange.externalExchangeDTOs;

import arpad.bank.bankbackend.dbmodel.TypeOfMutatie;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@Data
public class TransferResponse {
	private String transferNumber;
	private boolean transferSuccessful;
	private String transferNonSuccessfulReason;
	private String rekeningnummer;
	private String tegenRekeningNummer;
	private BigDecimal amount;
	private TypeOfMutatie typeOfMutatie;
}
