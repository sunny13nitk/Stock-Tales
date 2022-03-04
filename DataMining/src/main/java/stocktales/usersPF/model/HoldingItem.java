package stocktales.usersPF.model;

import java.sql.Date;

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
import stocktales.usersPF.enums.EnumTxnType;

@Entity
@Table(name = "holdingitems")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoldingItem
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int hlid;
	
	private Date date;
	
	private EnumTxnType txntype;
	
	private int units;
	
	private double ppu; //Includes Brokerage
	
	@ManyToOne(cascade =
	{ CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "hid")
	private Holding holdingHeader;
}
