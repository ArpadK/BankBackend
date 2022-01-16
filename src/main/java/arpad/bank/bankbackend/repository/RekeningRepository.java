package arpad.bank.bankbackend.repository;

import arpad.bank.bankbackend.dbmodel.Klant;
import arpad.bank.bankbackend.dbmodel.Rekening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RekeningRepository extends JpaRepository<Rekening, Long> {

	@Query("SELECT r FROM Rekening r LEFT JOIN FETCH r.klant WHERE r.rekeningnummer = ?1 ")
	public Rekening getRekeningByRekeningNummer(String rekeningNummer);

	@Query("SELECT r FROM Rekening r Where r.klant = ?1 AND r.rekeningnummer = ?2")
	public List<Rekening> getAllRekeningenFromKlantAndRekeningNummer(Klant klant, String rekeningNummer);
}
