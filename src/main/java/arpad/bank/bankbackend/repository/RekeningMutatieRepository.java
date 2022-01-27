package arpad.bank.bankbackend.repository;

import arpad.bank.bankbackend.dbmodel.Rekening;
import arpad.bank.bankbackend.dbmodel.RekeningMutatie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RekeningMutatieRepository extends JpaRepository<RekeningMutatie, Long> {

	/**
	 * Use this method to get the RekeningMutatie for a given transferNumber. Also specify the rekeningNummer because if it is an internal transfer, there will be
	 * two mutaties with the same number.
	 * @param transferNumber
	 * @param rekeningNummer
	 * @return The rekening mutitie
	 */
	@Query("SELECT rm FROM RekeningMutatie rm WHERE rm.transferNumber = ?1 AND rm.rekening.rekeningnummer = ?2")
	public RekeningMutatie getRekeningMutatieByTransferNumberAndRekeningNummer(String transferNumber, String rekeningNummer);
}
