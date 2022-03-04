package stocktales.scripsEngine.utilities.dateStringutils.JAXB.definitions;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Months")
@XmlAccessorType(XmlAccessType.FIELD)
public class Months
{
	private ArrayList<monthsConfig> monthConfig;

	public ArrayList<monthsConfig> getMonthConfig()
	{
		return monthConfig;
	}

	public void setMonthConfig(ArrayList<monthsConfig> monthConfig)
	{
		this.monthConfig = monthConfig;
	}

	public Months(ArrayList<monthsConfig> monthConfig)
	{
		super();
		this.monthConfig = monthConfig;
	}

	public Months()
	{
		this.monthConfig = new ArrayList<monthsConfig>();
	}

}
