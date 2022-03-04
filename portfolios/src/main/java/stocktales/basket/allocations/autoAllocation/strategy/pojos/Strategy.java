package stocktales.basket.allocations.autoAllocation.strategy.pojos;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "strategy")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Strategy
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int stid;
	
	private String name;
	
	private String concept;
	
	private String predicatebean;
	
	private boolean active;
	
	private boolean rebalanceallowed;
	
	private Date lastupdate;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "strategy")
	private List<StAllocItem> allocItems = new ArrayList<StAllocItem>();
	
	/*
	 * Helper Method to set association on Allocation Item for Strategy
	 */
	public Strategy addAllocationItem(
	        StAllocItem allocItem
	)
	{
		allocItem.setStrategy(this);
		this.allocItems.add(allocItem);
		return this;
	}
}
