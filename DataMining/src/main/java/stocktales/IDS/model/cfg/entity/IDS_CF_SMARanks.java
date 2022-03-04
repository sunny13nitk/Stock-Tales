package stocktales.IDS.model.cfg.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CF_SMARanks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IDS_CF_SMARanks
{
	@Id
	private String smaname;

	private int rank;
}
