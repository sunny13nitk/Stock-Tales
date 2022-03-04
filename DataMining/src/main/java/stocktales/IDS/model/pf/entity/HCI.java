package stocktales.IDS.model.pf.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.usersPF.enums.EnumTxnType;

@Entity
@Table(name = "HCI")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HCI
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int tid;
	@NotNull
	@Length(min = 3)
	private String sccode;

	private Date date;
	@Enumerated(EnumType.STRING)
	private EnumTxnType txntype;
	@Positive
	private int units;
	@Positive
	private double txnppu;
	@PositiveOrZero
	private int smarank;
}
