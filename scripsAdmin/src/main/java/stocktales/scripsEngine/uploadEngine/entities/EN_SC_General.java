package stocktales.scripsEngine.uploadEngine.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * 
 * Scrip Root Object
 *
 */
@Entity
@Table(name = "TB_SC_General")
public class EN_SC_General
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
	private double MktCap;
	private double MCapToSales;
	private double EPS;
	private double PEG;
	private double CPS;
	private double SGToCapex;
	private double NumShares;
	private double DERatio;
	private double TTMSales;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "SCCode")
	private List<EN_SC_BalSheet> balSheetEntList;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "SCCode")
	private List<EN_SC_Trends> trendsEntList;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "SCCode")
	private List<EN_SC_Quarters> qtrEntList;
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "SCCode")
	private EN_SC_10YData tenYrEnt;
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "SCCode")
	private EN_SC_Last4QData last4QEnt;
	
	public List<EN_SC_Trends> getTrendsEntList(
	)
	{
		return trendsEntList;
	}
	
	public void setTrendsEntList(
	        List<EN_SC_Trends> trendsEntList
	)
	{
		this.trendsEntList = trendsEntList;
	}
	
	public List<EN_SC_Quarters> getQtrEntList(
	)
	{
		return qtrEntList;
	}
	
	public void setQtrEntList(
	        List<EN_SC_Quarters> qtrEntList
	)
	{
		this.qtrEntList = qtrEntList;
	}
	
	public EN_SC_10YData getTenYrEnt(
	)
	{
		return tenYrEnt;
	}
	
	public void setTenYrEnt(
	        EN_SC_10YData tenYrEnt
	)
	{
		this.tenYrEnt = tenYrEnt;
	}
	
	public EN_SC_Last4QData getLast4QEnt(
	)
	{
		return last4QEnt;
	}
	
	public void setLast4QEnt(
	        EN_SC_Last4QData last4qEnt
	)
	{
		last4QEnt = last4qEnt;
	}
	
	public List<EN_SC_BalSheet> getBalSheetEntList(
	)
	{
		return balSheetEntList;
	}
	
	public void setBalSheetEntList(
	        List<EN_SC_BalSheet> balSheetEnt
	)
	{
		this.balSheetEntList = balSheetEnt;
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
	
	public EN_SC_General(
	)
	{
		super();
		// TODO Auto-generated constructor stub
	}
	
	/*
	 * UTILITY METHODS
	 */
	
	// Add BalanceSheet Entity
	public void add_EN_SC_BalSheet_Entity(
	        EN_SC_BalSheet balSheetEnt
	)
	{
		if (balSheetEnt != null)
		{
			if (this.balSheetEntList == null)
			{
				this.balSheetEntList = new ArrayList<EN_SC_BalSheet>();
				
			}
			
			balSheetEnt.setSCCode(this.getSCCode());
			this.balSheetEntList.add(balSheetEnt);
		}
	}
	
	// Add Trends Entity
	public void add_EN_SC_Trends_Entity(
	        EN_SC_Trends trendsEnt
	)
	{
		if (trendsEnt != null)
		{
			if (this.trendsEntList == null)
			{
				this.trendsEntList = new ArrayList<EN_SC_Trends>();
			}
			trendsEnt.setSCCode(this.getSCCode());
			this.trendsEntList.add(trendsEnt);
			
		}
	}
	
	// Add Quarters Entity
	public void add_EN_SC_Quarters_Entity(
	        EN_SC_Quarters qtrEnt
	)
	{
		if (qtrEnt != null)
		{
			if (this.qtrEntList == null)
			{
				this.qtrEntList = new ArrayList<EN_SC_Quarters>();
			}
			qtrEnt.setSCCode(this.getSCCode());
			this.qtrEntList.add(qtrEnt);
			
		}
	}
	
	// Add 10Y Entity
	public void add_EN_SC_10YData_Entity(
	        EN_SC_10YData data10YEnt
	)
	{
		if (data10YEnt != null)
		{
			data10YEnt.setSCCode(this.getSCCode());
			this.setTenYrEnt(data10YEnt);
		}
	}
	
	// Add Last 4Q Entity
	public void add_EN_SC_Last4QData_Entity(
	        EN_SC_Last4QData last4QEnt
	)
	{
		if (last4QEnt != null)
		{
			last4QEnt.setSCCode(this.getSCCode());
			this.setLast4QEnt(last4QEnt);
		}
	}
}
