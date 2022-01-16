package arpad.bank.bankbackend.dbmodel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
public class Particulier extends Klant{

	@Column
	@Getter @Setter
	private String BSN;

	@Column
	@Getter @Setter
	private LocalDate birthDate;


}
