package stocktales.dataBook.model.entity.sectors;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.dataBook.enums.EnumUnit;
import stocktales.dataBook.model.pojo.PJInterval;

@Entity
@Table(name = "fpfinancial_sector")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FPFinancialSector extends PJInterval
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long     id;
	private String   sccode;
	@Enumerated(EnumType.STRING)
	private EnumUnit unit;
	private double   aum;
	private double   totalincome;
	private double   feecommincome;
	private double   feecommexpenses;
	private double   provisions;
	private double   casheq;
	@Max(value = 100)
	private double   gnpa;
	@Max(value = 100)
	private double   nnpa;
	@Max(value = 100)
	private double   car;
	@Max(value = 100)
	private double   tier1car;
	@Max(value = 100)
	private double   pcr;
	private double   deposits;
	@Max(value = 100)
	private double   deptoconsborrow;
	@Max(value = 100)
	private double   retaildeppercentage;
	@Max(value = 100)
	private double   corpdeppercentage;
	@Max(value = 100)
	private double   opextonii;
	
}
