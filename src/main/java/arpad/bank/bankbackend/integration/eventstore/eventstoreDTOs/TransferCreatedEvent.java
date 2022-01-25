package arpad.bank.bankbackend.integration.eventstore.eventstoreDTOs;

import arpad.bank.bankbackend.dbmodel.TypeOfMutatie;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferCreatedEvent {
	private String transferNumber;
	private String rekeningNummer;
	private String tegenRekeningNummer;
	private BigDecimal amount;
	private TypeOfMutatie typeOfMutatie;
}
