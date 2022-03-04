package stocktales.IDS.model.pf.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "HC")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HC
{
	@Id
	private String sccode;
	private double ppu;
	private int units;
	private double dividend;

}
