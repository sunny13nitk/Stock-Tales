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
@Table(name = "userstrategies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStrategy
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private int stid;
	
	private boolean active; //False means no more money can be deployed
	
	private boolean needsrebalance; //Needs to be re-balanced after Stgy Rebalancing
	
	@ManyToOne(cascade =
	{ CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "cid")
	private UserPFConfig userpfconfig;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "userStrategy")
	private List<Holding> holdings = new ArrayList<Holding>();
	
	public UserStrategy addHolding(
	        Holding holding
	)
	{
		holding.setUserStrategy(this);
		this.holdings.add(holding);
		return this;
	}
	
}
