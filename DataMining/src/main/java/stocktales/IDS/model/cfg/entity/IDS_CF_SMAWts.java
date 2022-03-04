package stocktales.IDS.model.cfg.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CF_SMAWts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IDS_CF_SMAWts
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private double wtsma1;
	private double wtsma2;
	private double wtsma3;
	private double wtsma4;

}
