package stocktales.basket.allocations.autoAllocation.strategy.pojos;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "allocations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StAllocItem
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int     allocid;
	private String  sccode;
	private double  alloc;
	private double  mos;
	private boolean adhoc;  //IF Scrip is an Adhoc Scrip
	
	@ManyToOne(cascade =
	{ CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "stid")
	private Strategy strategy;
}
