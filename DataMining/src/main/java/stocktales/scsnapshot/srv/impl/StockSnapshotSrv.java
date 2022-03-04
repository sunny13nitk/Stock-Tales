package stocktales.scsnapshot.srv.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import stocktales.basket.allocations.autoAllocation.facades.interfaces.EDRCFacade;
import stocktales.basket.allocations.autoAllocation.valuations.interfaces.SCValuationSrv;
import stocktales.dataBook.model.entity.scLinks.ScLinks;
import stocktales.dataBook.model.repo.scLinks.RepoScLinks;
import stocktales.durations.UtilDurations;
import stocktales.healthcheck.beanSrv.intf.IScDataContSrv;
import stocktales.maths.UtilPercentages;
import stocktales.money.UtilDecimaltoMoneyString;
import stocktales.scripCalc.intf.IScripCalculations;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_BalSheet;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_GeneralQ;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_Last4QData;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_Quarters;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_Trends;
import stocktales.scripsEngine.uploadEngine.scDataContainer.services.interfaces.ISCDataContainerSrv;
import stocktales.scsnapshot.model.pojo.StockBSGranularNos;
import stocktales.scsnapshot.model.pojo.StockBSPerShareData;
import stocktales.scsnapshot.model.pojo.StockBSRatiosData;
import stocktales.scsnapshot.model.pojo.StockBalSheetData;
import stocktales.scsnapshot.model.pojo.StockFundamentals;
import stocktales.scsnapshot.model.pojo.StockMCapLast4QData;
import stocktales.scsnapshot.model.pojo.StockPETrends;
import stocktales.scsnapshot.model.pojo.StockQtrNos;
import stocktales.scsnapshot.model.pojo.StockQuoteBasic;
import stocktales.scsnapshot.model.pojo.StockRevWCCycleTrends;
import stocktales.scsnapshot.model.pojo.StockSnapshot;
import stocktales.scsnapshot.model.pojo.StockSnapshotMsgs;
import stocktales.scsnapshot.model.pojo.StockTrends;
import stocktales.scsnapshot.model.pojo.StockTrendsGeneral;
import stocktales.scsnapshot.model.pojo.StockValuationsI;
import stocktales.scsnapshot.model.pojo.StockWCDetails;
import stocktales.scsnapshot.srv.intf.IStockSnapshotCalcAttr_DESrv;
import stocktales.scsnapshot.srv.intf.IStockSnapshotSrv;
import stocktales.scsnapshot.srv.intf.IStockSnapshotTextsHelper;
import stocktales.services.interfaces.ScripService;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

@Service
public class StockSnapshotSrv implements IStockSnapshotSrv
{
	@Autowired
	private IScDataContSrv scBalSheetSrv;
	
	@Autowired
	private RepoScLinks repoScLinks;
	
	@Autowired
	private MessageSource msgSrc;
	
	@Autowired
	private ScripService scSrv;
	
	@Autowired
	private ISCDataContainerSrv scDCSrv;
	
	@Autowired
	private SCValuationSrv scValSrv;
	
	@Autowired
	private EDRCFacade edrcSrv;
	
	@Autowired
	private IScripCalculations scCalcSrv;
	
	@Autowired
	@Qualifier("DE_WorkingCapitalSrv")
	private IStockSnapshotCalcAttr_DESrv wcapDetailSrv;
	
	@Autowired
	@Qualifier("DE_QualityofGrowthSrv")
	private IStockSnapshotCalcAttr_DESrv qualGrowthSrv;
	
	@Autowired
	@Qualifier("SSTH_GranularTrends")
	private IStockSnapshotTextsHelper granNosTHSrv;
	
	private StockSnapshot ss;
	
	@Override
	public StockSnapshot getStockSnapshot(
	        String scCode
	) throws Exception
	{
		if (scCode != null && repoScLinks != null)
		{
			Optional<ScLinks> SCLO = repoScLinks.findBySccode(scCode);
			
			Stock stock = YahooFinance.get(scCode + ".NS");
			if (stock != null)
			{
				ss = new StockSnapshot();
				
				if (SCLO.isPresent())
				{
					ss.setScLinks(SCLO.get());
				}
				
				MathContext m1 = new MathContext(1);
				MathContext m0 = new MathContext(0);
				
				StockQuoteBasic ssB = new StockQuoteBasic();
				ssB.setScCode(scCode);
				
				ssB.setBvps(stock.getStats().getBookValuePerShare().round(m1));
				ssB.setCmp(stock.getQuote().getPrice().round(m0));
				ssB.setDma50(stock.getQuote().getPriceAvg50().round(m0));
				ssB.setDma200(stock.getQuote().getPriceAvg200().round(m0));
				ssB.setEpsCurr(stock.getStats().getEps().round(m1));
				ssB.setHigh52W(stock.getQuote().getYearHigh().round(m0));
				ssB.setLow52W(stock.getQuote().getYearLow().round(m0));
				ssB.setMCap(stock.getStats().getMarketCap().round(m0));
				ssB.setMCapTxt(UtilDecimaltoMoneyString.getMoneyStringforDecimal(ssB.getMCap().doubleValue(), 0));
				ssB.setName(stock.getName());
				ssB.setPbRatio(stock.getStats().getPriceBook().round(m1));
				ssB.setPeRatio(stock.getStats().getPe().round(m1));
				ssB.setPerChg200DMA(stock.getQuote().getChangeFromAvg200InPercent().round(m0));
				ssB.setPerChg50DMA(stock.getQuote().getChangeFromAvg50InPercent().round(m0));
				ssB.setPerChg52WkHigh(stock.getQuote().getChangeFromYearHighInPercent().round(m0));
				ssB.setPerChg52WkLow(stock.getQuote().getChangeFromYearLowInPercent().round(m0));
				
				if (scDCSrv != null)
				{
					scDCSrv.load(scCode);
					if (scDCSrv.getScDC() != null)
					{
						ssB.setSector(scDCSrv.getScDC().getSCRoot().getSector());
						ssB.setUph(scDCSrv.getScDC().getSCRoot().getUPH());
						ssB.setPeg(scDCSrv.getScDC().getSCRoot().getPEG());
						ssB.setCps(Precision.round(scDCSrv.getScDC().getSCRoot().getCPS(), 0));
						if (scDCSrv.getScDC().getTenYData() != null)
						{
							
							double fcf           = scDCSrv.getScDC().getTenYData().getFCF();
							double deltaMCap10Yr = scDCSrv.getScDC().getTenYData().getMcapI10Y();
							double cffld         = (fcf / deltaMCap10Yr) * 100;
							
							ssB.setCFOPAT(scDCSrv.getScDC().getTenYData().getCFOPATR());
							ssB.setFCFCFO(scDCSrv.getScDC().getTenYData().getFCFCFOR());
							
							ssB.setFcfyield(Precision.round(cffld, 1));
							
							ssB.setValratio(Precision.round(scDCSrv.getScDC().getTenYData().getValR(), 1));
						}
						
						if (scSrv != null)
						{
							ssB.setFinancial(scSrv.isScripBelongingToFinancialSector(scCode));
						}
						
					}
					
				}
				
				this.ss.setQuoteBasic(ssB);
				populateTargetPrice();
				populateEDRCSummary();
				populateValuations();
				populateLast4QData();
				populateLast10QData();
				populateFundamentals();
				populateTrends();
				/**
				 * Triggered in ASPECT ScripCalculationsCustomAttributesAspect via Service 
				 * ScripSnapshotCalcAttrSrv that implements IScripCalculations
				 */
				populateCustomAttrs();
				
				/*
				 * Now, the following methods can use Calculated Custom Attributes
				 */
				if (!ss.getQuoteBasic().isFinancial())
				{
					populateWCDetails();
				}
				
				populateBSData();
				
				populateQualityofGrowth();
				
			}
		}
		
		return ss;
	}
	
	private void populateLast10QData(
	)
	{
		if (this.scDCSrv.getScDC() != null)
		{
			List<EN_SC_Quarters> qtrSheetL = this.scDCSrv.getScDC().getQuarters_L();
			if (qtrSheetL != null)
			{
				if (qtrSheetL.size() > 0)
				{
					
					List<EN_SC_Quarters> last10Q    = null;
					List<EN_SC_Quarters> allQSorted = null;
					
					allQSorted = qtrSheetL.stream()
					        .sorted(Comparator.comparingLong(EN_SC_Quarters::getQTR_ID).reversed())
					        .collect(Collectors.toList());
					last10Q    = allQSorted.stream().limit(10).collect(Collectors.toList());
					
					if (last10Q != null)
					{
						if (last10Q.size() > 0)
						{
							int idx = 0;
							for (EN_SC_Quarters qtrEnt : last10Q)
							{
								if (idx == 0)
								{
									this.ss.setLatestQ(UtilDurations.getQuarterNamefromNumber(qtrEnt.getYearQ()));
								}
								
								StockQtrNos stQtrNos = new StockQtrNos();
								
								stQtrNos.setQtr(UtilDurations.getQuarterNamefromNumber(qtrEnt.getYearQ()));
								stQtrNos.setSales(Precision.round(qtrEnt.getSalesQ(), 0));
								stQtrNos.setNp(Precision.round(qtrEnt.getNPQ(), 0));
								stQtrNos.setOi(Precision.round(qtrEnt.getOI(), 0));
								stQtrNos.setNpm(Precision.round(stQtrNos.getNp() / stQtrNos.getSales(), 1));
								
								this.ss.getLast10QNos().add(stQtrNos);
								idx++;
							}
						}
					}
					
				}
			}
		}
	}
	
	private void populateBSData(
	)
	{
		int baseYR = UtilDurations.getYearsFromHistory(10).getYearFrom();
		
		if (baseYR > 0)
		{
			if (this.scDCSrv.getScDC() != null)
			{
				List<EN_SC_BalSheet> balSheetL = this.scDCSrv.getScDC().getBalSheet_L();
				if (balSheetL != null)
				{
					if (balSheetL.size() > 0)
					{
						this.ss.setBalSheetData(new StockBalSheetData());
						for (EN_SC_BalSheet balSheetI : balSheetL)
						{
							if (balSheetI.getYear() >= baseYR)
							{
								this.ss.getBalSheetData().getBalSheetGrData()
								        .add(new StockBSGranularNos(balSheetI.getYear(),
								                String.valueOf(balSheetI.getYear()), balSheetI.getSales(),
								                balSheetI.getPAT(), balSheetI.getCFO(),
								                balSheetI.getCFO() - balSheetI.getCapex(), balSheetI.getCashI(),
								                balSheetI.getRE(), balSheetI.getDebt()));
								
								this.ss.getBalSheetData().getEpsData()
								        .add(new StockBSPerShareData(String.valueOf(balSheetI.getYear()),
								                Precision.round(balSheetI.getEPS(), 1),
								                Precision.round(balSheetI.getcips(), 1),
								                Precision.round(balSheetI.getdivps(), 1)));
								
								this.ss.getBalSheetData().getBalSheetRatios()
								        .add(new StockBSRatiosData(String.valueOf(balSheetI.getYear()),
								                balSheetI.getOPM(), balSheetI.getNPM(), balSheetI.getROE(),
								                balSheetI.getROCE(), balSheetI.getcfoyield(),
								                balSheetI.getdividendpayout(), balSheetI.gettaxper()));
								
							}
						}
						
						if (this.granNosTHSrv != null && ss.getMsgs() != null)
						{
							this.ss.getMsgs().setBsGranNos(
							        granNosTHSrv.getMessagesforObject(
							                this.ss.getBalSheetData().getBalSheetGrData()).get(0));
						}
					}
				}
			}
		}
		
	}
	
	/*
	 * Populate the Target Price
	 */
	private void populateTargetPrice(
	)
	{
		if (scValSrv != null && this.ss.getQuoteBasic().getCmp().doubleValue() > 0)
		{
			this.ss.setTargetPrice(scValSrv.getValuationforScrip(this.ss.getQuoteBasic().getScCode(),
			        this.ss.getQuoteBasic().getCmp().doubleValue(), 1));
			if (ss.getQuoteBasic().getCps() > 0 && ss.getTargetPrice().getCMP() > 0)
			{
				this.ss.getTargetPrice().setCashCmpRatio(
				        Precision.round(((ss.getQuoteBasic().getCps() / ss.getTargetPrice().getCMP() * 100)), 1));
			}
		}
	}
	
	/*
	 * Populate EDRC Summary
	 */
	private void populateEDRCSummary(
	)
	{
		if (edrcSrv != null)
		{
			this.ss.setSummary(edrcSrv.getEDRCforSCrip(this.ss.getQuoteBasic().getScCode()));
		}
	}
	
	private void populateLast4QData(
	)
	{
		if (this.ss.getQuoteBasic().getMCap().doubleValue() > 0)
		{
			if (this.scDCSrv.getScDC() != null)
			{
				EN_SC_Last4QData enLast4QData = this.scDCSrv.getScDC().getLast4QData();
				if (enLast4QData != null)
				{
					BigDecimal ttmsalesCr = new BigDecimal(enLast4QData.getSales4Q() * 10000000);
					if (ttmsalesCr.doubleValue() > 0)
					{
						this.ss.setLast4QData(new StockMCapLast4QData());
						this.ss.getLast4QData().setMcapToSales(Precision.round(
						        (this.ss.getQuoteBasic().getMCap().doubleValue() / ttmsalesCr.doubleValue()), 1));
						this.ss.getLast4QData().setOPM(enLast4QData.getOPM4Q());
						this.ss.getLast4QData().setNPM(enLast4QData.getNPM4Q());
						
					}
					
				}
			}
		}
	}
	
	private void populateFundamentals(
	)
	{
		if (this.scDCSrv.getScDC() != null)
		{
			Optional<EN_SC_Trends> trends3YO = this.scDCSrv.getScDC().getTrends_L().stream()
			        .filter(x -> x.getPeriod().equals("3Yr")).findFirst();
			if (trends3YO.isPresent())
			{
				EN_SC_Trends trends3Y = trends3YO.get();
				if (trends3Y != null)
				{
					this.ss.setFundamentals(new StockFundamentals());
					this.ss.getFundamentals().setROCE3Y(trends3Y.getROCEAvg());
					this.ss.getFundamentals().setROE3Y(trends3Y.getROEAvg());
					
					this.ss.getFundamentals().setRecvInvBySales3Y(trends3Y.getWCCAvg());
					this.ss.getFundamentals().setIntDepByPAT3Y(trends3Y.getFViabAvg());
					
					EN_SC_GeneralQ enRoot = this.scDCSrv.getScDC().getSCRoot();
					if (enRoot != null)
					{
						this.ss.getFundamentals().setSGToCapex(Precision.round(enRoot.getSGToCapex() * 100, 0));
						this.ss.getFundamentals().setDeRatio(Precision.round(enRoot.getDERatio(), 2));
					}
					
					this.ss.getFundamentals().setDivGrowth3Y(trends3Y.getDivGR());
					this.ss.getFundamentals().setDivPayout3Y(trends3Y.getDivPayAvg());
					
					this.ss.getFundamentals().setAvgSSGR3Y(trends3Y.getSSGRAvg());
					this.ss.getFundamentals().setSalesG3Y(trends3Y.getSalesGR());
					
					if (!ss.getQuoteBasic().isFinancial()) //Only for Non Financials
					{
						if (ss.getQuoteBasic().getCFOPAT() > 0 && ss.getQuoteBasic().getFCFCFO() > 0)
						{
							ss.getFundamentals().setPATtoFCF(Precision.round(
							        (ss.getQuoteBasic().getCFOPAT() * ss.getQuoteBasic().getFCFCFO()) / 10000, 2));
							
							if (ss.getLast4QData() != null)
							{
								ss.getFundamentals().setREVtoFCF(Precision.round(
								        (ss.getFundamentals().getPATtoFCF() * ss.getLast4QData().getNPM()) / 100, 2));
							}
							
							String msgText = msgSrc.getMessage("FCF_INF", new Object[]
							{ ss.getFundamentals().getPATtoFCF(), ss.getLast4QData().getNPM(),
							        ss.getFundamentals().getREVtoFCF() }, Locale.ENGLISH);
							if (msgText != null)
							{
								if (ss.getMsgs() == null)
								{
									ss.setMsgs(new StockSnapshotMsgs());
								}
								ss.getMsgs().setFcf_infMsg(msgText);
							}
						}
					}
					
				}
			}
		}
	}
	
	private void populateTrends(
	)
	{
		if (this.scDCSrv.getScDC() != null)
		{
			List<EN_SC_Trends> trendsList = scDCSrv.getScDC().getTrends_L();
			if (trendsList != null)
			{
				if (trendsList.size() > 0)
				{
					this.ss.setTrends(new StockTrends());
					
					for (EN_SC_Trends trend : trendsList)
					{
						/*
						 *  New Trends - Initialize
						 */
						
						/*
						 * REvenue Working Capital Interval Trends
						 */
						
						if (!trend.getPeriod().equalsIgnoreCase("TTM"))
						{
							StockTrendsGeneral stGen = new StockTrendsGeneral();
							stGen.setPeriod(trend.getPeriod());
							stGen.setSalesG(trend.getSalesGR());
							stGen.setPatG(trend.getPATGR());
							stGen.setOpm(trend.getOPMAvg());
							stGen.setFcfCfoAvg(trend.getFCF_CFO_Avg());
							stGen.setDivPayO(trend.getDivPayAvg());
							stGen.setFiRatio(trend.getFViabAvg());
							
							this.ss.getTrends().getTrendsGen().add(stGen);
							
						}
						
						StockRevWCCycleTrends revWCTrend = new StockRevWCCycleTrends();
						revWCTrend.setPeriod(trend.getPeriod());
						revWCTrend.setRevenue(trend.getSalesGR());
						revWCTrend.setRecv(trend.getRecvTR());
						revWCTrend.setWcByrev(trend.getWCCAvg());
						revWCTrend.setSsgr(trend.getSSGRAvg());
						
						/*
						 * EPS and PE Trends
						 */
						StockPETrends peTrend = new StockPETrends();
						peTrend.setPtype(trend.getPeriod());
						peTrend.setAvgpe(Precision.round(trend.getAvgPE(), 1));
						peTrend.setEpsgr(trend.getPATGR());
						peTrend.setPegr(trend.getPEGR());
						
						/*
						 * Adding to Final Structures Here
						 */
						
						this.ss.getTrends().getRevWCTrends().add(revWCTrend);
						this.ss.getTrends().getPeTrends().add(peTrend);
						
					}
					
				}
			}
		}
	}
	
	private void populateValuations(
	)
	{
		if (this.ss.getQuoteBasic() != null)
		{
			StockValuationsI scValICMP = new StockValuationsI("CMP",
			        Precision.round(ss.getQuoteBasic().getCmp().doubleValue(), 0), 0);
			ss.getValuations().add(scValICMP);
			
			StockValuationsI scValI50DMA = new StockValuationsI("50 DMA",
			        Precision.round(ss.getQuoteBasic().getDma50().doubleValue(), 0),
			        Precision.round(ss.getQuoteBasic().getPerChg50DMA().doubleValue(), 0));
			ss.getValuations().add(scValI50DMA);
			
			StockValuationsI scValI200DMA = new StockValuationsI("200 DMA",
			        Precision.round(ss.getQuoteBasic().getDma200().doubleValue(), 0),
			        Precision.round(ss.getQuoteBasic().getPerChg200DMA().doubleValue(), 0));
			ss.getValuations().add(scValI200DMA);
			
			StockValuationsI scValI52wH = new StockValuationsI("52W High",
			        Precision.round(ss.getQuoteBasic().getHigh52W().doubleValue(), 0),
			        Precision.round(ss.getQuoteBasic().getPerChg52WkHigh().doubleValue(), 0));
			ss.getValuations().add(scValI52wH);
			
			StockValuationsI scValI52wL = new StockValuationsI("52W Low",
			        Precision.round(ss.getQuoteBasic().getLow52W().doubleValue(), 0),
			        Precision.round(ss.getQuoteBasic().getPerChg52WkLow().doubleValue(), 0));
			ss.getValuations().add(scValI52wL);
			
			// Get Trends for Scrip
			if (scDCSrv != null)
			{
				if (scDCSrv.getScDC() != null)
				{
					if (scDCSrv.getScDC().getTrends_L() != null)
					{
						
						//3yr
						Optional<EN_SC_Trends> trend3Y = scDCSrv.getScDC().getTrends_L().stream()
						        .filter(x -> x.getPeriod().equals("3Yr")).findFirst();
						if (trend3Y.isPresent())
						{
							
							double           price3Yr  = Precision.round(
							        ss.getQuoteBasic().getEpsCurr().doubleValue() * trend3Y.get().getAvgPE(), 0);
							StockValuationsI scValI3Yr = new StockValuationsI("3Y PE", price3Yr, UtilPercentages
							        .getPercentageDelta(ss.getQuoteBasic().getCmp().doubleValue(), price3Yr, 1));
							ss.getValuations().add(scValI3Yr);
							
						}
						
						//5yr
						Optional<EN_SC_Trends> trend5Y = scDCSrv.getScDC().getTrends_L().stream()
						        .filter(x -> x.getPeriod().equals("5Yr")).findFirst();
						if (trend5Y.isPresent())
						{
							
							double           price5Yr  = Precision.round(
							        ss.getQuoteBasic().getEpsCurr().doubleValue() * trend5Y.get().getAvgPE(), 0);
							StockValuationsI scValI5Yr = new StockValuationsI("5Y PE", price5Yr, UtilPercentages
							        .getPercentageDelta(ss.getQuoteBasic().getCmp().doubleValue(), price5Yr, 1));
							ss.getValuations().add(scValI5Yr);
							
						}
						
						//7yr
						Optional<EN_SC_Trends> trend7Y = scDCSrv.getScDC().getTrends_L().stream()
						        .filter(x -> x.getPeriod().equals("7Yr")).findFirst();
						if (trend7Y.isPresent())
						{
							
							double           price7Yr  = Precision.round(
							        ss.getQuoteBasic().getEpsCurr().doubleValue() * trend7Y.get().getAvgPE(), 0);
							StockValuationsI scValI7Yr = new StockValuationsI("7Y PE", price7Yr, UtilPercentages
							        .getPercentageDelta(ss.getQuoteBasic().getCmp().doubleValue(), price7Yr, 1));
							ss.getValuations().add(scValI7Yr);
							
						}
						
						//10yr
						Optional<EN_SC_Trends> trend10Y = scDCSrv.getScDC().getTrends_L().stream()
						        .filter(x -> x.getPeriod().equals("10Yr")).findFirst();
						if (trend10Y.isPresent())
						{
							
							double           price10Yr  = Precision.round(
							        ss.getQuoteBasic().getEpsCurr().doubleValue() * trend10Y.get().getAvgPE(), 0);
							StockValuationsI scValI10Yr = new StockValuationsI("10Y PE", price10Yr, UtilPercentages
							        .getPercentageDelta(ss.getQuoteBasic().getCmp().doubleValue(), price10Yr, 1));
							ss.getValuations().add(scValI10Yr);
							
						}
						
						//Weighted PE
						if (ss.getTargetPrice().getWeightedPE() > 0)
						{
							double           pricewtPE  = Precision.round(
							        ss.getQuoteBasic().getEpsCurr().doubleValue() * ss.getTargetPrice().getWeightedPE(),
							        0);
							StockValuationsI scValIWTPe = new StockValuationsI("Wt. PE", pricewtPE, UtilPercentages
							        .getPercentageDelta(ss.getQuoteBasic().getCmp().doubleValue(), pricewtPE, 1));
							ss.getValuations().add(scValIWTPe);
						}
						
					}
				}
			}
			
		}
	}
	
	@SuppressWarnings("unchecked")
	private void populateWCDetails(
	)
	{
		if (wcapDetailSrv != null)
		{
			ss.setWcDetails(
			        (List<StockWCDetails>) wcapDetailSrv.extractStockSnapshotData(scDCSrv, ss.getScCalcAttrResult()));
		}
	}
	
	private void populateQualityofGrowth(
	)
	{
		if (qualGrowthSrv != null)
		{
			
		}
	}
	
	/*
	 * Populate Custom Attributes Configured in SRv Bean which implements IScripCalculations
	 */
	private void populateCustomAttrs(
	)
	{
		if (scCalcSrv != null)
		{
			this.ss.setScCalcAttrResult(
			        scCalcSrv.getScripCalculatedAttributesResult(this.ss.getQuoteBasic().getScCode()));
			
			//			for (TY_AttrMultiContainer multiAttrCont : this.ss.getScCalcAttrResult().getAttrsMulti())
			//			{
			//				System.out.println(multiAttrCont.getAttrName());
			//				
			//				for (NameVal nameVal : multiAttrCont.getNameVals())
			//				{
			//					System.out.println(nameVal.getName() + "-----" + nameVal.getValue());
			//				}
			//			}
		}
	}
}
