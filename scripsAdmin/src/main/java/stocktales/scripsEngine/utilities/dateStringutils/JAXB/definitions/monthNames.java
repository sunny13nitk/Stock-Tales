package stocktales.scripsEngine.utilities.dateStringutils.JAXB.definitions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "monthNames")
@XmlAccessorType(XmlAccessType.FIELD)
public class monthNames
{
	private String	sname;

	private String	lname;

	public String getSname()
	{
		return sname;
	}

	public void setSname(String sname)
	{
		this.sname = sname;
	}

	public String getLname()
	{
		return lname;
	}

	public void setLname(String lname)
	{
		this.lname = lname;
	}

	public monthNames(String sname, String lname)
	{
		super();
		this.sname = sname;
		this.lname = lname;
	}

	public monthNames()
	{
		super();
		// TODO Auto-generated constructor stub
	}

}
