package arpad.bank.bankbackend.dbmodel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class Zakelijk extends Klant{

	@Column
	@Getter @Setter
	private int KVKNummer;

}
