package stocktales.topgun.model.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TopGun")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TopGun
{
	@Id
	private String nsecode;
	private int    rankincl;
	private int    rankcurr;
	private double priceincl;
	
}
