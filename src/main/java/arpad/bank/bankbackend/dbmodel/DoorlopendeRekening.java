package arpad.bank.bankbackend.dbmodel;

import arpad.bank.bankbackend.repository.RekeningRepository;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
@Slf4j
@NoArgsConstructor
public class DoorlopendeRekening extends Rekening{

	/**
	 * Checks with the given tegenRekeningNummer if the transfer is legal given the Bank businessrules.
	 * @param internalTransfer a boolean indicating if you are transferring money within the same bank.
	 * @param tegenRekeningNummer the rekeningnummer of the tegenrekening
	 * @param amount the amount you want to transfer
	 * @param typeOfMutatie specify if you are depositing or withdrawing from this account.
	 * @param rekeningRepository an instance of the rekeningRepository
	 * @return a boolean indicating if the transfer is legal.
	 */
	@Override
	public boolean checkIfTransferIsLegal(boolean internalTransfer, String tegenRekeningNummer, BigDecimal amount, TypeOfMutatie typeOfMutatie, RekeningRepository rekeningRepository) {
		log.info("Checking if transfer is legal on DoorlopendeRekening");
		if(
			saldoCheck(amount, typeOfMutatie)
		){
			log.info("Transfer is legal");
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Checks if the saldo would be below 0 after the transfer for a Persenal DoolopendeRekening or below 5000 for a Zakelijke DoorlopendeRekening.
	 * @param amount The amount that should be transferred
	 * @param typeOfMutatie The type of mutation
	 * @return A boolean indicating if the transfer is legal
	 */
	private boolean saldoCheck(BigDecimal amount, TypeOfMutatie typeOfMutatie){
		if(typeOfMutatie == TypeOfMutatie.AF) {
			if (klant instanceof Particulier) {
				if (saldo.compareTo(amount) < 0) {
					log.info("Transfer is illegal: transfer would make the saldo of this persoonlijke rekening go lower then 0");
					return false;
				}
			}
			if (klant instanceof Zakelijk){
				BigDecimal minValueZakelijk = new BigDecimal(-5000);
				if (saldo.subtract(amount).compareTo(minValueZakelijk) < 0) {
					log.info("Transfer is illegal: transfer would make the saldo of this zakelijke rekening go lower then 5000");
					return false;
				}
			}
		}
		return true;
	}
}
