package stocktales.DataLake.model.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "DLATH")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DL_ScripPriceATH
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGen")
	@SequenceGenerator(name = "seqGen", allocationSize = 1)
	private long id;
	private String sccode;
	private Date date;
	private double closeprice;

	public DL_ScripPriceATH(String sccode, Date date, double closeprice)
	{
		super();
		this.sccode = sccode;
		this.date = date;
		this.closeprice = closeprice;
	}

}
