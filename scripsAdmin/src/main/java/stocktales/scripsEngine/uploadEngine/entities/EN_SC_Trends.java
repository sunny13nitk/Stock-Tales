package stocktales.scripsEngine.uploadEngine.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TB_SC_Trends")
public class EN_SC_Trends
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int Trend_ID;
	private String SCCode;
	private String period;
	private int SalesGR;
	private int RecvTR;
	private int OPMAvg; // Renamed
	private int PATGR;
	private int PEGR; // Added
	private int DivGR;
	private int DebtGR;
	private int BVGR;
	private double AvgPE;
	@Column(name = "div_pay_avg")
	private double DivPayAvg; // Renamed
	private double ROCEAvg; // Renamed
	private double ROEAvg; // Added
	@Column(name = "fcf_cfo_avg")
	private int FCF_CFO_Avg; // Renamed
	private int SSGRAvg; // Added
	@Column(name = "fviab_avg")
	private int FViabAvg; // Added
	private double WCCAvg; // Added
	private double CFOGrowth; // Added

	public int getTrend_ID()
	{
		return Trend_ID;
	}

	public void setTrend_ID(int trend_ID)
	{
		Trend_ID = trend_ID;
	}

	public double getROEAvg()
	{
		return ROEAvg;
	}

	public void setROEAvg(double rOEAvg)
	{
		ROEAvg = rOEAvg;
	}

	public double getCFOGrowth()
	{
		return CFOGrowth;
	}

	public void setCFOGrowth(double cFOGrowth)
	{
		CFOGrowth = cFOGrowth;
	}

	public int getSSGRAvg()
	{
		return SSGRAvg;
	}

	public void setSSGRAvg(int sSGRAvg)
	{
		SSGRAvg = sSGRAvg;
	}

	public int getFViabAvg()
	{
		return FViabAvg;
	}

	public void setFViabAvg(int fViabAvg)
	{
		FViabAvg = fViabAvg;
	}

	public double getWCCAvg()
	{
		return WCCAvg;
	}

	public void setWCCAvg(double wCCAvg)
	{
		WCCAvg = wCCAvg;
	}

	public String getSCCode()
	{
		return SCCode;
	}

	public void setSCCode(String sCCode)
	{
		SCCode = sCCode;
	}

	public String getPeriod()
	{
		return period;
	}

	public void setPeriod(String period)
	{
		this.period = period;
	}

	public int getSalesGR()
	{
		return SalesGR;
	}

	public int getRecvTR()
	{
		return RecvTR;
	}

	public void setRecvTR(int recvTR)
	{
		RecvTR = recvTR;
	}

	public void setSalesGR(int salesGR)
	{
		SalesGR = salesGR;
	}

	public int getPATGR()
	{
		return PATGR;
	}

	public void setPATGR(int pATGR)
	{
		PATGR = pATGR;
	}

	public int getDivGR()
	{
		return DivGR;
	}

	public void setDivGR(int divGR)
	{
		DivGR = divGR;
	}

	public int getDebtGR()
	{
		return DebtGR;
	}

	public void setDebtGR(int debtGR)
	{
		DebtGR = debtGR;
	}

	public int getBVGR()
	{
		return BVGR;
	}

	public void setBVGR(int bVGR)
	{
		BVGR = bVGR;
	}

	public double getAvgPE()
	{
		return AvgPE;
	}

	public void setAvgPE(double avgPE)
	{
		AvgPE = avgPE;
	}

	public int getOPMAvg()
	{
		return OPMAvg;
	}

	public void setOPMAvg(int oPMAvg)
	{
		OPMAvg = oPMAvg;
	}

	public int getPEGR()
	{
		return PEGR;
	}

	public void setPEGR(int pEGR)
	{
		PEGR = pEGR;
	}

	public double getDivPayAvg()
	{
		return DivPayAvg;
	}

	public void setDivPayAvg(double divPayAvg)
	{
		DivPayAvg = divPayAvg;
	}

	public double getROCEAvg()
	{
		return ROCEAvg;
	}

	public void setROCEAvg(double rOCEAvg)
	{
		ROCEAvg = rOCEAvg;
	}

	public int getFCF_CFO_Avg()
	{
		return FCF_CFO_Avg;
	}

	public void setFCF_CFO_Avg(int fCF_CFO_Avg)
	{
		FCF_CFO_Avg = fCF_CFO_Avg;
	}

	public EN_SC_Trends()
	{
		super();
		// TODO Auto-generated constructor stub
	}

}
