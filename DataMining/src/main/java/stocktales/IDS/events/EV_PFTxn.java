package stocktales.IDS.events;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;
import stocktales.IDS.model.pf.entity.HCI;

@Getter
@Setter
public class EV_PFTxn extends ApplicationEvent
{

	private HCI pfTxn;

	public EV_PFTxn(Object source, HCI pfTxn)
	{
		super(source);
		this.pfTxn = pfTxn;
	}

}
