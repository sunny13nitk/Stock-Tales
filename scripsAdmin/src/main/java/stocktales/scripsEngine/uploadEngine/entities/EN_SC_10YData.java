package stocktales.scripsEngine.uploadEngine.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TB_SC_10YData")
public class EN_SC_10YData
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int ID_10YData;
	
	private String SCCode;
	private double PATG10Y;
	private double CFOG10;
	private int    CFOPATR;
	private double CapexG10Y;
	private int    FCF;
	private int    FCFCFOR;
	private int    Div10Y;
	private int    CashI10Y;
	private int    RE10Y;
	private int    McapI10Y;
	private double ValR;
	
	public int getID_10YData(
	)
	{
		return ID_10YData;
	}
	
	public void setID_10YData(
	        int iD_10YData
	)
	{
		ID_10YData = iD_10YData;
	}
	
	public String getSCCode(
	)
	{
		return SCCode;
	}
	
	public void setSCCode(
	        String sCCode
	)
	{
		SCCode = sCCode;
	}
	
	public double getPATG10Y(
	)
	{
		return PATG10Y;
	}
	
	public void setPATG10Y(
	        double pATG10Y
	)
	{
		PATG10Y = pATG10Y;
	}
	
	public double getCFOG10(
	)
	{
		return CFOG10;
	}
	
	public void setCFOG10(
	        double cFOG10
	)
	{
		CFOG10 = cFOG10;
	}
	
	public int getCFOPATR(
	)
	{
		return CFOPATR;
	}
	
	public void setCFOPATR(
	        int cFOPATR
	)
	{
		CFOPATR = cFOPATR;
	}
	
	public double getCapexG10Y(
	)
	{
		return CapexG10Y;
	}
	
	public void setCapexG10Y(
	        double capexG10Y
	)
	{
		CapexG10Y = capexG10Y;
	}
	
	public int getFCF(
	)
	{
		return FCF;
	}
	
	public void setFCF(
	        int fCF
	)
	{
		FCF = fCF;
	}
	
	public int getFCFCFOR(
	)
	{
		return FCFCFOR;
	}
	
	public void setFCFCFOR(
	        int fCFCFOR
	)
	{
		FCFCFOR = fCFCFOR;
	}
	
	public int getDiv10Y(
	)
	{
		return Div10Y;
	}
	
	public void setDiv10Y(
	        int div10y
	)
	{
		Div10Y = div10y;
	}
	
	public int getCashI10Y(
	)
	{
		return CashI10Y;
	}
	
	public void setCashI10Y(
	        int cashI10Y
	)
	{
		CashI10Y = cashI10Y;
	}
	
	public int getRE10Y(
	)
	{
		return RE10Y;
	}
	
	public void setRE10Y(
	        int rE10Y
	)
	{
		RE10Y = rE10Y;
	}
	
	public int getMcapI10Y(
	)
	{
		return McapI10Y;
	}
	
	public void setMcapI10Y(
	        int mcapI10Y
	)
	{
		McapI10Y = mcapI10Y;
	}
	
	public double getValR(
	)
	{
		return ValR;
	}
	
	public void setValR(
	        double valR
	)
	{
		ValR = valR;
	}
	
	public EN_SC_10YData(
	)
	{
		super();
		// TODO Auto-generated constructor stub
	}
	
	public EN_SC_10YData(
	        String sCCode, double pATG10Y, double cFOG10, int cFOPATR, double capexG10Y, int fCF, int fCFCFOR,
	        int div10y, int cashI10Y, int rE10Y, int mcapI10Y, double valR
	)
	{
		super();
		SCCode    = sCCode;
		PATG10Y   = pATG10Y;
		CFOG10    = cFOG10;
		CFOPATR   = cFOPATR;
		CapexG10Y = capexG10Y;
		FCF       = fCF;
		FCFCFOR   = fCFCFOR;
		Div10Y    = div10y;
		CashI10Y  = cashI10Y;
		RE10Y     = rE10Y;
		McapI10Y  = mcapI10Y;
		ValR      = valR;
	}
	
}
