package arpad.bank.bankbackend.repository;

import arpad.bank.bankbackend.dbmodel.RekeningMutatie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RekeningMutatieRepository extends JpaRepository<RekeningMutatie, Long> {

}
