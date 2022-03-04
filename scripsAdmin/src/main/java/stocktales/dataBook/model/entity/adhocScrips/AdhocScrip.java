package stocktales.dataBook.model.entity.adhocScrips;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "AdhocScrip")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdhocScrip
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String sccode;
	private String name;
	private String sector;
}
