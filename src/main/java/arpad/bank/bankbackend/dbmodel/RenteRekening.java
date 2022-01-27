package arpad.bank.bankbackend.dbmodel;

import arpad.bank.bankbackend.exceptions.TransferIllegalException;
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
	 */
	@Override
	public void checkIfTransferIsLegal(boolean internalTransfer, String tegenRekeningNummer, BigDecimal amount, TypeOfMutatie typeOfMutatie, RekeningRepository rekeningRepository) throws TransferIllegalException {
		log.info("Checking if transfer is legal on RenteRekening");
		checkIncomingTransfer(typeOfMutatie);
		checkSaldo(amount, typeOfMutatie);
		checkIsOwnTegenRekening(tegenRekeningNummer, rekeningRepository);
		log.info("Transfer is legal");
	}

	/**
	 * Checks If the transfer is depositing money into this account. This is not allowed on a renteRekening
	 * @param typeOfMutatie The type of mutation
	 */
	private void checkIncomingTransfer(TypeOfMutatie typeOfMutatie) throws TransferIllegalException {
		if (typeOfMutatie == TypeOfMutatie.BIJ) {
			throw new TransferIllegalException("It is not allowed to transfer money to a renterekening");
		}
	}

	/**
	 * Checks if the saldo would be below 0 after the transfer. This is not allowed on a Renterekening.
	 * @param amount The amount that should be transferred
	 * @param typeOfMutatie The type of mutation
	 */
	private void checkSaldo(BigDecimal amount, TypeOfMutatie typeOfMutatie) throws TransferIllegalException {
		if(typeOfMutatie == TypeOfMutatie.AF) {
			if (saldo.compareTo(amount) < 0) {
				throw new TransferIllegalException("Saldo can't be lower then 0 on a Rente rekening");
			}
		}
	}

	/**
	 * Checks if the transfer is to a tegenRekening That is also owned by the same klant. You can't wire money from a renterekening to an other klants rekening
	 * @param tegenRekeningNummer The number of the tegenRekening
	 * @param rekeningRepository An instance of the RekeningRepository
	 */
	private void checkIsOwnTegenRekening(String tegenRekeningNummer, RekeningRepository rekeningRepository) throws TransferIllegalException {
		boolean isOwnTegenRekening = !rekeningRepository.getAllRekeningenFromKlantAndRekeningNummer(this.klant, tegenRekeningNummer).isEmpty();
		if(!isOwnTegenRekening){
			throw new TransferIllegalException("Can't transfer money from a Renterekening to a rekening that is not owned by the same klant");
		}
	}
}