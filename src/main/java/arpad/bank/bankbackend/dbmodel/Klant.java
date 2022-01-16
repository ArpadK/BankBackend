package arpad.bank.bankbackend.dbmodel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
public abstract class Klant {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter @Setter
	protected Long id;

	@OneToMany(mappedBy = "klant")
	@Getter @Setter
	protected List<Rekening> rekeningen = new ArrayList<>();

	@Column
	@Getter @Setter
	protected String adress;

}
