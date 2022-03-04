package stocktales.tracker.services;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_GeneralQ;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_Trends;
import stocktales.scripsEngine.uploadEngine.scDataContainer.DAO.types.scDataContainer;
import stocktales.scripsEngine.uploadEngine.scDataContainer.services.interfaces.ISCDataContainerSrv;
import stocktales.services.interfaces.ScripService;
import stocktales.tracker.interfaces.ITrackerSrv;
import stocktales.tracker.pojos.ScTrackerHead;
import stocktales.tracker.pojos.ScTrackerPrices;
import stocktales.tracker.pojos.ScTrackerRevPAT;
import stocktales.tracker.pojos.ScTrakerResult;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TrackerSrv implements ITrackerSrv
{
	@Autowired
	private ISCDataContainerSrv scDataContSrv;
	
	@Autowired
	private ScripService scSrv;
	
	private List<String> scripsList = new ArrayList<String>();
	
	private List<scDataContainer> scripsDataBank = new ArrayList<scDataContainer>();
	
	private List<EN_SC_GeneralQ> scripsRoot = new ArrayList<EN_SC_GeneralQ>();
	
	private ScTrakerResult scTrackerResult;
	
	private String[] trendsPeriods = new String[]
	{ "5Yr", "3Yr", "TTM" };
	
	@Override
	public ScTrakerResult getforScrips(
	        String[] scrips
	) throws Exception
	{
		//Initialize
		initialize();
		
		//Validate Scrips 
		validateScrips(scrips);
		
		//Process Scrips Comparison
		processComparison();
		
		return this.scTrackerResult;
	}
	
	@Override
	public ScTrakerResult getforStrategy(
	        int StrategyId
	)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * Initialize all Buffer Vars - Session Bean
	 */
	private void initialize(
	)
	{
		this.scripsList      = new ArrayList<String>();
		this.scripsRoot      = new ArrayList<EN_SC_GeneralQ>();
		this.scripsDataBank  = new ArrayList<scDataContainer>();
		this.scTrackerResult = null;
		
	}
	
	/*
	 * Validate the Scrips from List - Only Valid ones to be retained
	 */
	private void validateScrips(
	        String[] scrips
	)
	{
		if (scrips != null && scrips.length > 0)
		{
			if (scSrv != null)
			{
				for (String scrip : scrips)
				{
					EN_SC_GeneralQ scRoot = scSrv.getScripHeader(scrip);
					
					if (scRoot != null)
					{
						scripsList.add(scrip);
						scripsRoot.add(scRoot);
					}
				}
			}
		}
		
	}
	
	/*
	 * Process - Scrips Comparison
	 */
	private void processComparison(
	) throws Exception
	{
		//At least 2 Scrips
		if (this.scripsList.size() > 1)
		{
			this.scTrackerResult = new ScTrakerResult(); //REsult Instance Creation
			getTrackerHeaders_Prices(); //Process Tracker Header
			getTrackerRevPAT(); // Process REvenue and PAT Tracker for Scrips
			
		}
		
	}
	
	private void getTrackerHeaders_Prices(
	) throws Exception
	{
		for (String scrip : this.scripsList)
		{
			Stock stock = YahooFinance.get(scrip + ".NS");
			if (stock != null)
			{
				MathContext  m1 = new MathContext(1);
				MathContext  m0 = new MathContext(0, RoundingMode.CEILING);
				NumberFormat nf = NumberFormat.getNumberInstance();
				nf.setRoundingMode(RoundingMode.CEILING);
				
				ScTrackerHead   scTH = new ScTrackerHead();
				ScTrackerPrices scTP = new ScTrackerPrices();
				BigDecimal      Cr   = new BigDecimal(10000000);
				
				scTH.setScCode(scrip);
				scTH.setMCap(Precision.round((stock.getStats().getMarketCap().doubleValue() / 10000000), 0));
				scTH.setMCapStr(
				        nf.format(scTH.getMCap()));
				//(stock.getStats().getMarketCap().divide(Cr)).setScale(0, RoundingMode.CEILING).toPlainString());
				scTH.setPECurr(stock.getStats().getPe().round(m1).doubleValue());
				
				EN_SC_GeneralQ scRoot = this.scripsRoot.stream().filter(x -> x.getSCCode().equals(scrip)).findFirst()
				        .get();
				if (scRoot != null)
				{
					scTH.setPEG(Precision.round(scRoot.getPEG() * (scTH.getPECurr() / scRoot.getCurrPE()), 1)); //Latest PEG
					scTH.setUPH(scRoot.getUPH());
					scTH.setFinancial(scSrv.isScripBelongingToFinancialSector(scrip));
				}
				
				scDataContSrv.load(scrip);
				scDataContainer scDCon = scDataContSrv.getScDC();
				if (scDCon != null)
				{
					scTH.setSalesL4Q(Precision.round(scDCon.getLast4QData().getSales4Q(), 0));
					
					scTH.setSalesL4QStr(nf.format(scTH.getSalesL4Q()));
					scTH.setMCapBySales(Precision.round(scTH.getMCap() / scTH.getSalesL4Q(), 1));
					
					this.scripsDataBank.add(scDCon);
				}
				
				this.scTrackerResult.getTrackerHeaders().add(scTH);
				
				scTP.setCmp(stock.getQuote().getPrice().setScale(0, RoundingMode.CEILING).toPlainString());
				scTP.setDma200(stock.getQuote().getPriceAvg200().setScale(0, RoundingMode.CEILING).toPlainString());
				scTP.setDma50(stock.getQuote().getPriceAvg50().setScale(0, RoundingMode.CEILING).toPlainString());
				scTP.setHigh52W(stock.getQuote().getYearHigh().setScale(0, RoundingMode.CEILING).toPlainString());
				scTP.setLow52W(stock.getQuote().getYearLow().setScale(0, RoundingMode.CEILING).toPlainString());
				
				this.scTrackerResult.getTrackerPrices().add(scTP);
				
			}
		}
		
		computeTrackerHeaderRatios();
		
	}
	
	private void computeTrackerHeaderRatios(
	)
	{
		if (this.scTrackerResult.getTrackerHeaders() != null)
		{
			if (this.scTrackerResult.getTrackerHeaders().size() > 0)
			{
				//Get Min'm Mcap
				double minMCap = this.scTrackerResult.getTrackerHeaders().stream()
				        .min(Comparator.comparing(ScTrackerHead::getMCap)).get().getMCap();
				
				if (minMCap > 0)
				{
					for (ScTrackerHead trHead : this.scTrackerResult.getTrackerHeaders())
					{
						trHead.setMCapR(Precision.round(trHead.getMCap() / minMCap, 1));
						
					}
				}
				
			}
		}
		
	}
	
	private void getTrackerRevPAT(
	)
	{
		if (this.scripsDataBank != null)
		{
			if (this.scripsDataBank.size() > 0)
			{
				for (scDataContainer scDCon : scripsDataBank)
				{
					List<EN_SC_Trends> scTrendsList = scDCon.getTrends_L();
					if (scTrendsList != null)
					{
						if (scTrendsList.size() > 0)
						{
							ScTrackerRevPAT scTrRevPat = new ScTrackerRevPAT();
							for (int i = 0; i < trendsPeriods.length; i++)
							{
								int                    w      = i;                                       //Array warning during compilation for scope Final
								Optional<EN_SC_Trends> trendO = scTrendsList.stream()
								        .filter(x -> x.getPeriod().equals(trendsPeriods[w])).findFirst();
								if (trendO.isPresent())
								{
									EN_SC_Trends enTrend = trendO.get();
									switch (w)
									{
										case 0:
											scTrRevPat.setRevG5Y(Precision.round(enTrend.getSalesGR(), 0));
											scTrRevPat.setPatG5Y(Precision.round(enTrend.getPATGR(), 0));
											break;
										
										case 1:
											scTrRevPat.setRevG3Y(Precision.round(enTrend.getSalesGR(), 0));
											scTrRevPat.setPatG3Y(Precision.round(enTrend.getPATGR(), 0));
											break;
										
										case 2:
											scTrRevPat.setRevGTTM(Precision.round(enTrend.getSalesGR(), 0));
											scTrRevPat.setPatGTTM(Precision.round(enTrend.getPATGR(), 0));
											break;
										
										default:
											break;
									}
								}
							}
							
							this.scTrackerResult.getTrackerRevPATs().add(scTrRevPat);
							
						}
					}
					
				}
			}
		}
	}
}
