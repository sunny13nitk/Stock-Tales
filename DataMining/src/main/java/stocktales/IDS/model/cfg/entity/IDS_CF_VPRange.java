package stocktales.IDS.model.cfg.entity;

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
@Table(name = "CF_VPRange")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IDS_CF_VPRange
{
	@Id
	@Enumerated(EnumType.STRING)
	private EnumVolatilityProfile profile;

	private double scoremin;

	private double scoremax;
}
