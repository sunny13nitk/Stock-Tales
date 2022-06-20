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
import stocktales.IDS.enums.EnumSMABreach;

@Entity
@Table(name = "PFVolProfileI")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PFVolProfileI
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String sccode;
	private Date datebreach;
	@Enumerated(EnumType.STRING)
	private EnumSMABreach smalvl;
	private double price;
}
