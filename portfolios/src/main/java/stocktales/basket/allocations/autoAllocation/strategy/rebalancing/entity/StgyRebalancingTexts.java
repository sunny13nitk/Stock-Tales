package stocktales.basket.allocations.autoAllocation.strategy.rebalancing.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.basket.allocations.autoAllocation.strategy.rebalancing.enums.ERebalType;

@Entity
@Table(name = "stgy_rebalancing_txts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StgyRebalancingTexts
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int        rbid;
	private int        stid;
	private String     sccode;
	@Enumerated(EnumType.STRING)
	private ERebalType rbaltype;
	@Lob
	private String     reason;
	@Lob
	private Byte[]     image;
}
