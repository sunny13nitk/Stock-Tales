package stocktales.IDS.events;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;
import stocktales.IDS.model.pf.entity.MoneyBag;

@Getter
@Setter
/**
 * Published in IDS_MoneyBagSrv Service method processMBagTxn(MoneyBag mBagTxn)
 */
public class EV_MoneyBagTxn extends ApplicationEvent
{

	private MoneyBag mbagTxn;

	public EV_MoneyBagTxn(Object source, MoneyBag mbagTxn)
	{
		super(source);
		this.mbagTxn = mbagTxn;
	}

}
