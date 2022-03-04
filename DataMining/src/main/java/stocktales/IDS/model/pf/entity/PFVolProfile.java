package stocktales.IDS.model.pf.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.IDS.enums.EnumVolatilityProfile;

@Entity
@Table(name = "PFVolProfile")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PFVolProfile
{
	@Id
	private String sccode;
	private int sma1b;
	private int sma2b;
	private int sma3b;
	private int sma4b;
	private double score;
	@Enumerated(EnumType.STRING)
	private EnumVolatilityProfile profile;
}
