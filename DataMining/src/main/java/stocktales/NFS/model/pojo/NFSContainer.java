package stocktales.NFS.model.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.NFS.model.entity.NFSJournal;
import stocktales.NFS.model.entity.NFSPF;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSContainer
{
	private List<NFSConsistency> baseDataPool = new ArrayList<NFSConsistency>();
	
	//Can be Shown
	private List<StockHistoryCmpNFS> consFltPassScrips_PriceHist = new ArrayList<StockHistoryCmpNFS>();
	
	//Can be Shown
	private List<NFSSmaCmp> priceTrendsDataPool = new ArrayList<NFSSmaCmp>();
	
	//Can be Shown
	private List<NFSPriceManipulation> manipulatedScrips = new ArrayList<NFSPriceManipulation>();
	
	//Can be Shown - Direct
	private List<NFSScores> finalSieveScores = new ArrayList<NFSScores>();
	
	private NFSStats nfsStats;
	
	private List<NFSPF> NFSPortfolio = new ArrayList<NFSPF>(); //Current PF in Buffer
	
	private List<NFSPF> RC = new ArrayList<NFSPF>(); //replacement Container
	
	private List<NFSPF> RT = new ArrayList<NFSPF>(); //replacement Target
	
	private NFSJournal NFSJEntity; //NFS Journal entity to be inserted in Journal
	
	private double cashTxnBalance;
	
	private double perPosInvestment;
	
}
