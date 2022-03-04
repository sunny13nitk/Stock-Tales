package stocktales.scripsEngine.uploadEngine.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(
        name = "TB_SC_Last4QData"
)
public class EN_SC_Last4QData
{
	@Id
	@GeneratedValue(
	        strategy = GenerationType.IDENTITY
	)
	private long   TTM_ID;
	private String SCCode;
	private double Sales4Q;
	private double OP4Q;
	private int    OPM4Q;
	private int    OI4Q;
	private int    EBITDA4Q;
	private int    Int4Q;
	private int    Dep4Q;
	private int    PBT4Q;
	private int    Tax4Q;
	private int    TaxP4Q;
	private double PAT4Q;
	private int    NPM4Q;
	private double EPS4Q;
	
	public long getTTM_ID(
	)
	{
		return TTM_ID;
	}
	
	public void setTTM_ID(
	        long tTM_ID
	)
	{
		TTM_ID = tTM_ID;
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
	
	public double getSales4Q(
	)
	{
		return Sales4Q;
	}
	
	public void setSales4Q(
	        double sales4q
	)
	{
		Sales4Q = sales4q;
	}
	
	public double getOP4Q(
	)
	{
		return OP4Q;
	}
	
	public void setOP4Q(
	        double oP4Q
	)
	{
		OP4Q = oP4Q;
	}
	
	public int getOPM4Q(
	)
	{
		return OPM4Q;
	}
	
	public void setOPM4Q(
	        int oPM4Q
	)
	{
		OPM4Q = oPM4Q;
	}
	
	public int getOI4Q(
	)
	{
		return OI4Q;
	}
	
	public void setOI4Q(
	        int oI4Q
	)
	{
		OI4Q = oI4Q;
	}
	
	public int getEBITDA4Q(
	)
	{
		return EBITDA4Q;
	}
	
	public void setEBITDA4Q(
	        int eBITDA4Q
	)
	{
		EBITDA4Q = eBITDA4Q;
	}
	
	public int getInt4Q(
	)
	{
		return Int4Q;
	}
	
	public void setInt4Q(
	        int int4q
	)
	{
		Int4Q = int4q;
	}
	
	public int getDep4Q(
	)
	{
		return Dep4Q;
	}
	
	public void setDep4Q(
	        int dep4q
	)
	{
		Dep4Q = dep4q;
	}
	
	public int getPBT4Q(
	)
	{
		return PBT4Q;
	}
	
	public void setPBT4Q(
	        int pBT4Q
	)
	{
		PBT4Q = pBT4Q;
	}
	
	public int getTax4Q(
	)
	{
		return Tax4Q;
	}
	
	public void setTax4Q(
	        int tax4q
	)
	{
		Tax4Q = tax4q;
	}
	
	public int getTaxP4Q(
	)
	{
		return TaxP4Q;
	}
	
	public void setTaxP4Q(
	        int taxP4Q
	)
	{
		TaxP4Q = taxP4Q;
	}
	
	public double getPAT4Q(
	)
	{
		return PAT4Q;
	}
	
	public void setPAT4Q(
	        double pAT4Q
	)
	{
		PAT4Q = pAT4Q;
	}
	
	public int getNPM4Q(
	)
	{
		return NPM4Q;
	}
	
	public void setNPM4Q(
	        int nPM4Q
	)
	{
		NPM4Q = nPM4Q;
	}
	
	public double getEPS4Q(
	)
	{
		return EPS4Q;
	}
	
	public void setEPS4Q(
	        double ePS4Q
	)
	{
		EPS4Q = ePS4Q;
	}
	
	public EN_SC_Last4QData(
	)
	{
		super();
		// TODO Auto-generated constructor stub
	}
	
	public EN_SC_Last4QData(
	        String sCCode, double sales4q, double oP4Q, int oPM4Q, int oI4Q, int eBITDA4Q, int int4q, int dep4q,
	        int pBT4Q, int tax4q, int taxP4Q, double pAT4Q, int nPM4Q, double ePS4Q
	)
	{
		super();
		SCCode   = sCCode;
		Sales4Q  = sales4q;
		OP4Q     = oP4Q;
		OPM4Q    = oPM4Q;
		OI4Q     = oI4Q;
		EBITDA4Q = eBITDA4Q;
		Int4Q    = int4q;
		Dep4Q    = dep4q;
		PBT4Q    = pBT4Q;
		Tax4Q    = tax4q;
		TaxP4Q   = taxP4Q;
		PAT4Q    = pAT4Q;
		NPM4Q    = nPM4Q;
		EPS4Q    = ePS4Q;
	}
	
}
