package arpad.bank.bankbackend.dbmodel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@NoArgsConstructor
public class RekeningMutatie {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter @Setter
	protected Long id;

	@Column
	@Getter @Setter
	protected String transferNumber;

	@ManyToOne
	@JoinColumn(name="rekening")
	@Getter @Setter
	protected Rekening rekening;

	@Column
	@Getter @Setter
	protected String tegenRekeningNummer;

	@Column
	@Getter @Setter
	// TODO: start using this field.
	protected String tegenRekeningNaam;

	@Column
	@Getter @Setter
	// TODO: start using this field.
	protected String omschrijving;

	@Column
	@Getter @Setter
	protected BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Getter @Setter
	protected TypeOfMutatie typeOfMutatie;

	@Enumerated(EnumType.STRING)
	@Getter @Setter
	protected MutatieStatus mutatieStatus;

	@Column
	@Getter @Setter
	protected Timestamp lastUpdateMutatieStatus;

	public RekeningMutatie(String transferNumber, String tegenRekeningNummer, TypeOfMutatie typeOfMutatie, BigDecimal amount, Rekening rekening, MutatieStatus mutatieStatus){
		this.transferNumber = transferNumber;
		this.tegenRekeningNummer = tegenRekeningNummer;
		this.typeOfMutatie = typeOfMutatie;
		this.amount = amount;
		this.rekening = rekening;
		this.mutatieStatus = mutatieStatus;
		this.lastUpdateMutatieStatus = new Timestamp(System.currentTimeMillis());
	}

	/**
	 * Changes the state for the mutation to completed and updates the last update timestamp
	 */
	public void finalizeMutation(){
		mutatieStatus = MutatieStatus.Completed;
		lastUpdateMutatieStatus = new Timestamp(System.currentTimeMillis());
	}

	/**
	 * Cancels the mutation and updates the last update timestamp
	 */
	public void cancelMutation(){
		mutatieStatus = MutatieStatus.Cancelled;
		lastUpdateMutatieStatus = new Timestamp(System.currentTimeMillis());
	}
}
