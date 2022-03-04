package stocktales.scripsEngine.uploadEngine.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * Scrip Root Object
 *
 */
@Entity
@Table(name = "TB_SC_General")
public class EN_SC_GeneralQ
{
	@Id
	private String SCCode;
	private String SCName;
	private String Sector;
	private double UPH;
	private double CMP;
	private double CurrPE;
	private double CurrPB;
	@Column(name = "div_yield")
	private double DivYield;
	@Column(name = "mkt_cap")
	private double MktCap;
	@Column(name = "mcap_to_sales")
	private double MCapToSales;
	private double EPS;
	private double PEG;
	private double CPS;
	@Column(name = "sgto_capex")
	private double SGToCapex;
	@Column(name = "num_shares")
	private double NumShares;
	private double DERatio;
	private double TTMSales;
	
	public double getDERatio(
	)
	{
		return DERatio;
	}
	
	public void setDERatio(
	        double dERatio
	)
	{
		DERatio = dERatio;
	}
	
	public String getSCCode(
	)
	{
		return SCCode;
	}
	
	public double getTTMSales(
	)
	{
		return TTMSales;
	}
	
	public void setTTMSales(
	        double tTMSales
	)
	{
		TTMSales = tTMSales;
	}
	
	public void setSCCode(
	        String sCCode
	)
	{
		SCCode = sCCode;
	}
	
	public String getSCName(
	)
	{
		return SCName;
	}
	
	public void setSCName(
	        String sCName
	)
	{
		SCName = sCName;
	}
	
	public String getSector(
	)
	{
		return Sector;
	}
	
	public void setSector(
	        String sector
	)
	{
		Sector = sector;
	}
	
	public double getUPH(
	)
	{
		return UPH;
	}
	
	public void setUPH(
	        double uPH
	)
	{
		UPH = uPH;
	}
	
	public double getCMP(
	)
	{
		return CMP;
	}
	
	public void setCMP(
	        double cMP
	)
	{
		CMP = cMP;
	}
	
	public double getCurrPE(
	)
	{
		return CurrPE;
	}
	
	public void setCurrPE(
	        double currPE
	)
	{
		CurrPE = currPE;
	}
	
	public double getCurrPB(
	)
	{
		return CurrPB;
	}
	
	public void setCurrPB(
	        double currPB
	)
	{
		CurrPB = currPB;
	}
	
	public double getDivYield(
	)
	{
		return DivYield;
	}
	
	public void setDivYield(
	        double divYield
	)
	{
		DivYield = divYield;
	}
	
	public double getMktCap(
	)
	{
		return MktCap;
	}
	
	public void setMktCap(
	        double mktCap
	)
	{
		MktCap = mktCap;
	}
	
	public double getMCapToSales(
	)
	{
		return MCapToSales;
	}
	
	public void setMCapToSales(
	        double mCapToSales
	)
	{
		MCapToSales = mCapToSales;
	}
	
	public double getEPS(
	)
	{
		return EPS;
	}
	
	public void setEPS(
	        double ePS
	)
	{
		EPS = ePS;
	}
	
	public double getPEG(
	)
	{
		return PEG;
	}
	
	public void setPEG(
	        double pEG
	)
	{
		PEG = pEG;
	}
	
	public double getCPS(
	)
	{
		return CPS;
	}
	
	public void setCPS(
	        double cPS
	)
	{
		CPS = cPS;
	}
	
	public double getSGToCapex(
	)
	{
		return SGToCapex;
	}
	
	public void setSGToCapex(
	        double sGToCapex
	)
	{
		SGToCapex = sGToCapex;
	}
	
	public double getNumShares(
	)
	{
		return NumShares;
	}
	
	public void setNumShares(
	        double numShares
	)
	{
		NumShares = numShares;
	}
	
	public EN_SC_GeneralQ(
	)
	{
		super();
		// TODO Auto-generated constructor stub
	}
	
}
