package stocktales.usersPF.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "holdings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Holding
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int hid;
	
	private String sccode;
	
	private int units;
	
	private double ppu;
	
	private double realzdiv;
	
	private double realzpl;
	
	@ManyToOne(cascade =
	{ CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "id")
	private UserStrategy userStrategy;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "holdingHeader")
	private List<HoldingItem> holdingItem = new ArrayList<HoldingItem>();
	
	public Holding addItem(
	        HoldingItem hlI
	)
	{
		hlI.setHoldingHeader(this);
		this.holdingItem.add(hlI);
		return this;
	}
	
}
