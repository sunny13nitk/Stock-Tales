package stocktales.scripsEngine.utilities.dateStringutils.JAXB.definitions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "monthsConfig")
@XmlAccessorType(XmlAccessType.FIELD)
public class monthsConfig
{

	private monthNames	monthNames;
	private int		quarter;
	private int		monValue;

	public monthNames getMonthNames()
	{
		return monthNames;
	}

	public void setMonthNames(monthNames names)
	{
		this.monthNames = names;
	}

	public int getQuarter()
	{
		return quarter;
	}

	public void setQuarter(int quarter)
	{
		this.quarter = quarter;
	}

	public int getMonValue()
	{
		return monValue;
	}

	public void setMonValue(int monValue)
	{
		this.monValue = monValue;
	}

	public monthsConfig(monthNames names, int quarter, int monValue)
	{
		super();
		this.monthNames = names;
		this.quarter = quarter;
		this.monValue = monValue;
	}

	public monthsConfig()
	{
		super();
		this.monthNames = new monthNames();

	}

}
