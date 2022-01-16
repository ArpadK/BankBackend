package arpad.bank.bankbackend.repository;

import arpad.bank.bankbackend.dbmodel.Klant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public interface KlantRepository extends JpaRepository<Klant, Long> {

}
