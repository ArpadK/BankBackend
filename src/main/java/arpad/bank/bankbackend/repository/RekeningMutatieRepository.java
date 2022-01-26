package arpad.bank.bankbackend.repository;

import arpad.bank.bankbackend.dbmodel.Rekening;
import arpad.bank.bankbackend.dbmodel.RekeningMutatie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RekeningMutatieRepository extends JpaRepository<RekeningMutatie, Long> {

	@Query("SELECT rm FROM RekeningMutatie rm WHERE rm.transferNumber = ?1 AND rm.rekening.rekeningnummer = ?2")
	public RekeningMutatie getRekeningMutatieByTransferNumberAndRekeningNummer(String transferNumber, String rekeningNummer);
}
