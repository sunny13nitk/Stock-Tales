package stocktales.scripsEngine.uploadEngine.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_sc_bal_sheet")
public class EN_SC_BalSheet
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "bal_sheet_id")
	private long BalSheet_ID;
	
	private int    year;        //REnamed
	private String SCCode;
	private int    Sales;
	private int    OP;
	private int    OPM;
	private int    OI;
	private int    EBITDA;
	private int    Interest;
	private int    Depreciation;
	private int    PBT;
	private int    Tax;
	//@Column(name = "tax_per")
	private int    taxper;
	private int    PAT;
	private int    NPM;
	private int    CFO;
	private int    Capex;
	private int    Debt;
	private int    CashI;
	private double EPS;
	private int    SSGR;
	private int    ROE;
	private int    ROCE;
	private double NFAT;
	private int    ReceivableD;
	private int    ITR;
	private int    NFA;
	private int    CWIP;
	//@Column(name = "share_capital")
	private int sharecapital;
	//@Column(name = "dividend_paid")
	private int dividendpaid;
	//@Column(name = "dividend_payout")
	private int    dividendpayout;
	private int    RE;
	private double PE;
	private int    MCap;
	//@Column(name = "face_value")
	private double facevalue;
	//@Column(name = "book_value")
	private double bookvalue;
	private int    Equity;
	private double DERatio;
	private double ICR;
	private int    CFI;
	private int    CFF;
	private int    NCF;
	//@Column(name = "rawmto_sales")
	private double rawmtosales;
	//@Column(name = "empcto_sales")
	private double empctosales;
	//@Column(name = "powfto_sales")
	private double powftosales;
	//@Column(name = "mktg_to_sales")
	private double mktgtosales;
	//@Column(name = "other_to_sales")
	private double othertosales;
	//@Column(name = "dep_to_sales")
	private double deptosales;
	
	private double cips; //Cash + Investments Per Share
	
	private int cfoyield; //CFO/(EV = Debt + Equity)
	
	private int recvbysales; //Receivables by Sales as Percentage
	
	private int invbysales; //Inventory by sales as percentage
	
	private double numshares; //No. of Shares in Cr.
	
	private double divps; //Dividend per Share
	
	public long getBalSheet_ID(
	)
	{
		return BalSheet_ID;
	}
	
	public void setBalSheet_ID(
	        long balSheet_ID
	)
	{
		BalSheet_ID = balSheet_ID;
	}
	
	public double getcips(
	)
	{
		return cips;
	}
	
	public void setcips(
	        double cips
	)
	{
		this.cips = cips;
	}
	
	public int getcfoyield(
	)
	{
		return cfoyield;
	}
	
	public void setcfoyield(
	        int cfoyield
	)
	{
		this.cfoyield = cfoyield;
	}
	
	public int getrecvbysales(
	)
	{
		return recvbysales;
	}
	
	public void setrecvbysales(
	        int recvbysales
	)
	{
		this.recvbysales = recvbysales;
	}
	
	public int getinvbysales(
	)
	{
		return invbysales;
	}
	
	public void setinvbysales(
	        int invbysales
	)
	{
		this.invbysales = invbysales;
	}
	
	public double getnumshares(
	)
	{
		return numshares;
	}
	
	public void setnumshares(
	        double numshares
	)
	{
		this.numshares = numshares;
	}
	
	public double getdivps(
	)
	{
		return divps;
	}
	
	public void setdivps(
	        double divps
	)
	{
		this.divps = divps;
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
	
	public int getYear(
	)
	{
		return year;
	}
	
	public void setYear(
	        int year
	)
	{
		this.year = year;
	}
	
	public int getSales(
	)
	{
		return Sales;
	}
	
	public void setSales(
	        int sales
	)
	{
		Sales = sales;
	}
	
	public double getfacevalue(
	)
	{
		return facevalue;
	}
	
	public void setfacevalue(
	        double faceValue
	)
	{
		facevalue = faceValue;
	}
	
	public double getbookvalue(
	)
	{
		return bookvalue;
	}
	
	public void setbookvalue(
	        double bookValue
	)
	{
		bookvalue = bookValue;
	}
	
	public int getOP(
	)
	{
		return OP;
	}
	
	public void setOP(
	        int oP
	)
	{
		OP = oP;
	}
	
	public int getOPM(
	)
	{
		return OPM;
	}
	
	public void setOPM(
	        int oPM
	)
	{
		OPM = oPM;
	}
	
	public int getOI(
	)
	{
		return OI;
	}
	
	public void setOI(
	        int oI
	)
	{
		OI = oI;
	}
	
	public int getEBITDA(
	)
	{
		return EBITDA;
	}
	
	public void setEBITDA(
	        int eBITDA
	)
	{
		EBITDA = eBITDA;
	}
	
	public int getInterest(
	)
	{
		return Interest;
	}
	
	public void setInterest(
	        int interest
	)
	{
		Interest = interest;
	}
	
	public int getDepreciation(
	)
	{
		return Depreciation;
	}
	
	public void setDepreciation(
	        int depreciation
	)
	{
		Depreciation = depreciation;
	}
	
	public int getPBT(
	)
	{
		return PBT;
	}
	
	public void setPBT(
	        int pBT
	)
	{
		PBT = pBT;
	}
	
	public int getTax(
	)
	{
		return Tax;
	}
	
	public void setTax(
	        int tax
	)
	{
		Tax = tax;
	}
	
	public int gettaxper(
	)
	{
		return taxper;
	}
	
	public void settaxper(
	        int taxper
	)
	{
		this.taxper = taxper;
	}
	
	public int getPAT(
	)
	{
		return PAT;
	}
	
	public void setPAT(
	        int pAT
	)
	{
		PAT = pAT;
	}
	
	public int getNPM(
	)
	{
		return NPM;
	}
	
	public void setNPM(
	        int nPM
	)
	{
		NPM = nPM;
	}
	
	public int getCFO(
	)
	{
		return CFO;
	}
	
	public void setCFO(
	        int cFO
	)
	{
		CFO = cFO;
	}
	
	public int getCapex(
	)
	{
		return Capex;
	}
	
	public void setCapex(
	        int capex
	)
	{
		Capex = capex;
	}
	
	public int getDebt(
	)
	{
		return Debt;
	}
	
	public void setDebt(
	        int debt
	)
	{
		Debt = debt;
	}
	
	public int getCashI(
	)
	{
		return CashI;
	}
	
	public void setCashI(
	        int cashI
	)
	{
		CashI = cashI;
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
	
	public int getSSGR(
	)
	{
		return SSGR;
	}
	
	public void setSSGR(
	        int sSGR
	)
	{
		SSGR = sSGR;
	}
	
	public int getROE(
	)
	{
		return ROE;
	}
	
	public void setROE(
	        int rOE
	)
	{
		ROE = rOE;
	}
	
	public int getROCE(
	)
	{
		return ROCE;
	}
	
	public void setROCE(
	        int rOCE
	)
	{
		ROCE = rOCE;
	}
	
	public int getNFA(
	)
	{
		return NFA;
	}
	
	public void setNFA(
	        int nFA
	)
	{
		NFA = nFA;
	}
	
	public double getNFAT(
	)
	{
		return NFAT;
	}
	
	public void setNFAT(
	        double nFAT
	)
	{
		NFAT = nFAT;
	}
	
	public int getReceivableD(
	)
	{
		return ReceivableD;
	}
	
	public void setReceivableD(
	        int receivableD
	)
	{
		ReceivableD = receivableD;
	}
	
	public int getITR(
	)
	{
		return ITR;
	}
	
	public void setITR(
	        int iTR
	)
	{
		ITR = iTR;
	}
	
	public int getCWIP(
	)
	{
		return CWIP;
	}
	
	public void setCWIP(
	        int cWIP
	)
	{
		CWIP = cWIP;
	}
	
	public int getsharecapital(
	)
	{
		return sharecapital;
	}
	
	public void setsharecapital(
	        int shareCapital
	)
	{
		sharecapital = shareCapital;
	}
	
	public int getdividendpaid(
	)
	{
		return dividendpaid;
	}
	
	public void setdividendpaid(
	        int dividendPaid
	)
	{
		this.dividendpaid = dividendPaid;
	}
	
	public int getdividendpayout(
	)
	{
		return dividendpayout;
	}
	
	public void setdividendpayout(
	        int dividendPayout
	)
	{
		this.dividendpayout = dividendPayout;
	}
	
	public int getRE(
	)
	{
		return RE;
	}
	
	public void setRE(
	        int rE
	)
	{
		RE = rE;
	}
	
	public double getPE(
	)
	{
		return PE;
	}
	
	public void setPE(
	        double pE
	)
	{
		PE = pE;
	}
	
	public int getMCap(
	)
	{
		return MCap;
	}
	
	public void setMCap(
	        int mCap
	)
	{
		MCap = mCap;
	}
	
	public int getEquity(
	)
	{
		return Equity;
	}
	
	public void setEquity(
	        int equity
	)
	{
		Equity = equity;
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
	
	public double getICR(
	)
	{
		return ICR;
	}
	
	public void setICR(
	        double iCR
	)
	{
		ICR = iCR;
	}
	
	public int getCFI(
	)
	{
		return CFI;
	}
	
	public void setCFI(
	        int cFI
	)
	{
		CFI = cFI;
	}
	
	public int getCFF(
	)
	{
		return CFF;
	}
	
	public void setCFF(
	        int cFF
	)
	{
		CFF = cFF;
	}
	
	public int getNCF(
	)
	{
		return NCF;
	}
	
	public void setNCF(
	        int nCF
	)
	{
		NCF = nCF;
	}
	
	public double getrawmtosales(
	)
	{
		return rawmtosales;
	}
	
	public void setrawmtosales(
	        double rawMToSales
	)
	{
		rawmtosales = rawMToSales;
	}
	
	public double getempctosales(
	)
	{
		return empctosales;
	}
	
	public void setempctosales(
	        double empCToSales
	)
	{
		empctosales = empCToSales;
	}
	
	public double getpowftosales(
	)
	{
		return powftosales;
	}
	
	public void setpowftosales(
	        double powFToSales
	)
	{
		powftosales = powFToSales;
	}
	
	public double getmktgtosales(
	)
	{
		return mktgtosales;
	}
	
	public void setmktgtosales(
	        double mktgToSales
	)
	{
		mktgtosales = mktgToSales;
	}
	
	public double getothertosales(
	)
	{
		return othertosales;
	}
	
	public void setothertosales(
	        double otherToSales
	)
	{
		othertosales = otherToSales;
	}
	
	public double getdeptosales(
	)
	{
		return deptosales;
	}
	
	public void setdeptosales(
	        double depToSales
	)
	{
		deptosales = depToSales;
	}
	
	public EN_SC_BalSheet(
	)
	{
		super();
		// TODO Auto-generated constructor stub
	}
	
}
