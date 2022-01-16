package arpad.bank.bankbackend.dbmodel;

import arpad.bank.bankbackend.repository.RekeningRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Slf4j
@NoArgsConstructor
public abstract class Rekening {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter @Setter
	protected Long id;

	@ManyToOne
	@JoinColumn(name="klant_id")
	@Getter @Setter
	protected Klant klant;

	@OneToMany(mappedBy = "rekening", cascade=CascadeType.ALL)
	@Getter
	protected List<RekeningMutatie> rekeningMutaties = new ArrayList<>();

	@Column
	@Getter @Setter
	protected String rekeningnummer;

	@Column
	@Getter @Setter
	protected BigDecimal saldo;

	/**
	 * Checks with the given tegenRekeningNummer if the transfer is legal given the Bank businessrules.
	 * @param internalTransfer a boolean indicating if you are transferring money within the same bank.
	 * @param tegenRekeningNummer the rekeningnummer of the tegenrekening
	 * @param amount the amount you want to transfer
	 * @param typeOfMutatie specify if you are depositing or withdrawing from this account.
	 * @param rekeningRepository an instance of the rekeningRepository
	 * @return a boolean indicating if the transfer is legal.
	 */
	public abstract boolean checkIfTransferIsLegal(boolean internalTransfer,String tegenRekeningNummer, BigDecimal amount, TypeOfMutatie typeOfMutatie, RekeningRepository rekeningRepository);

	/**
	 * Updates the saldo of this rekening with the amount specified
	 * @param typeOfMutatie Specify if you want to deposit or withdraw from this account.
	 * @param amount The amount you want to add or remove.
	 */
	public void updateSaldo(TypeOfMutatie typeOfMutatie, BigDecimal amount){
		if(typeOfMutatie == TypeOfMutatie.AF){
			saldo = saldo.subtract(amount);
		}
		if(typeOfMutatie == TypeOfMutatie.BIJ){
			saldo = saldo.add(amount);
		}
	}

	public void addRekeningMutatie(RekeningMutatie rekeningMutatie){
		this.rekeningMutaties.add(rekeningMutatie);
	}
}
