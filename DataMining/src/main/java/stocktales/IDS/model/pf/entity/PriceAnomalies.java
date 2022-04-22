package stocktales.IDS.model.pf.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PriceAnomalies")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PriceAnomalies
{
	@Id
	private String sccode;
}
