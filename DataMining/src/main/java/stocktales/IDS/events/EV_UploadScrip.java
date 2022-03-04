package stocktales.IDS.events;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EV_UploadScrip extends ApplicationEvent
{

	private String scCode;

	private String sourceTitle;

	public EV_UploadScrip(Object source, String scCode, String sourceTitle)
	{
		super(source);
		this.scCode = scCode;
		this.sourceTitle = sourceTitle;
	}

}
