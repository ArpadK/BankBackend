package arpad.bank.bankbackend.integration.eventstore.eventstoreDTOs;

import arpad.bank.bankbackend.dbmodel.TypeOfMutatie;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferEvent {
	private TransferEventType transferEventType;
	private String transferNumber;
	private String rekeningNummer;
	private String tegenRekeningNummer;
	private BigDecimal amount;
	private TypeOfMutatie typeOfMutatie;
}
