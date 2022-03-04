package stocktales.scripsEngine.uploadEngine.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TB_SC_Quarters")
public class EN_SC_Quarters
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long QTR_ID;
	private String SCCode;
	private String YearQ;
	private double SalesQ;
	private double Expenses;
	private double OI;
	private double PBTQ;
	private double NPQ;

	public long getQTR_ID()
	{
		return QTR_ID;
	}

	public void setQTR_ID(long qTR_ID)
	{
		QTR_ID = qTR_ID;
	}

	public String getSCCode()
	{
		return SCCode;
	}

	public void setSCCode(String sCCode)
	{
		SCCode = sCCode;
	}

	public String getYearQ()
	{
		return YearQ;
	}

	public void setYearQ(String yearQ)
	{
		YearQ = yearQ;
	}

	public double getSalesQ()
	{
		return SalesQ;
	}

	public void setSalesQ(double salesQ)
	{
		SalesQ = salesQ;
	}

	public double getExpenses()
	{
		return Expenses;
	}

	public void setExpenses(double expenses)
	{
		Expenses = expenses;
	}

	public double getOI()
	{
		return OI;
	}

	public void setOI(double oI)
	{
		OI = oI;
	}

	public double getPBTQ()
	{
		return PBTQ;
	}

	public void setPBTQ(double pBTQ)
	{
		PBTQ = pBTQ;
	}

	public double getNPQ()
	{
		return NPQ;
	}

	public void setNPQ(double nPQ)
	{
		NPQ = nPQ;
	}

	public EN_SC_Quarters()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public EN_SC_Quarters(String sCCode, String yearQ, double salesQ, double expenses, double oI, double pBTQ,
			double nPQ)
	{
		super();
		SCCode = sCCode;
		YearQ = yearQ;
		SalesQ = salesQ;
		Expenses = expenses;
		OI = oI;
		PBTQ = pBTQ;
		NPQ = nPQ;
	}

}
