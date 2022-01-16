package arpad.bank.bankbackend.dbmodel;

import arpad.bank.bankbackend.repository.RekeningRepository;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
@Slf4j
@NoArgsConstructor
public class RenteRekening extends Rekening{

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
		log.info("Checking if transfer is legal on RenteRekening");
		if(
				checkIncomingTransfer(typeOfMutatie) &&
				checkSaldo(amount, typeOfMutatie) &&
				checkIsOwnTegenRekening(tegenRekeningNummer, rekeningRepository)
		){
			log.info("Transfer is legal");
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Checks If the transfer is depositing money into this account. This is not allowed on a renteRekening
	 * @param typeOfMutatie The type of mutation
	 * @return  A boolean indicating if the transfer is legal
	 */
	private boolean checkIncomingTransfer(TypeOfMutatie typeOfMutatie){
		if (typeOfMutatie == TypeOfMutatie.BIJ) {
			log.info("transfer is illegal: it's not allowed to transfer money to a renteRekening");
			return false;
		}
		return true;
	}

	/**
	 * Checks if the saldo would be below 0 after the transfer. This is not allowed on a Renterekening.
	 * @param amount The amount that should be transferred
	 * @param typeOfMutatie The type of mutation
	 * @return A boolean indicating if the transfer is legal
	 */
	private boolean checkSaldo(BigDecimal amount, TypeOfMutatie typeOfMutatie){
		if(typeOfMutatie == TypeOfMutatie.AF) {
			if (saldo.compareTo(amount) < 0) {
				log.info("Transfer is illegal: transfer would make the saldo of this Renterekeing rekening go lower then 0");
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the transfer is to a tegenRekening That is also owned by the same klant. You can't wire money from a renterekening to an other klants rekening
	 * @param tegenRekeningNummer The number of the tegenRekening
	 * @param rekeningRepository An instance of the RekeningRepository
	 * @return A boolean indicating if the transfer is leagal.
	 */
	private boolean checkIsOwnTegenRekening(String tegenRekeningNummer, RekeningRepository rekeningRepository){
		boolean isOwnTegenRekening = !rekeningRepository.getAllRekeningenFromKlantAndRekeningNummer(this.klant, tegenRekeningNummer).isEmpty();
		if(!isOwnTegenRekening){
			log.info("transfer is illegal: it is not allowed to transfer money from/to a rekening that is now yours from/to a renteRekening");
			return false;
		}
		return true;
	}
}