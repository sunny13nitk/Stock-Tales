package stocktales.IDS.model.pf.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.IDS.enums.EnumTxnType;

@Entity
@Table(name = "MoneyBag")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MoneyBag
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int tid;
	private Date date;
	@Enumerated(EnumType.STRING)
	private EnumTxnType type;
	private double amount;
	private String remarks; // Should be Scrip Code in case of Dividend
}
