package stocktales.IDS.srv.impl;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import stocktales.IDS.enums.EnumSMABreach;
import stocktales.IDS.model.pf.entity.HC;
import stocktales.IDS.model.pf.entity.PFSchema;
import stocktales.IDS.model.pf.repo.RepoHC;
import stocktales.IDS.model.pf.repo.RepoHCI;
import stocktales.IDS.model.pf.repo.RepoPFSchema;
import stocktales.IDS.pojo.IDS_SMAPreview;
import stocktales.IDS.pojo.UI.IDS_BuyProposalBO;
import stocktales.IDS.pojo.UI.PFDBContainer;
import stocktales.IDS.pojo.UI.PFHoldingsPL;
import stocktales.IDS.pojo.UI.PFStatsH;
import stocktales.IDS.pojo.UI.ScripPLSS;
import stocktales.IDS.srv.intf.IDS_PFSchema_REbalUI_Srv;
import stocktales.IDS.utility.SMASortUtility;
import stocktales.NFS.enums.EnumMCapClassification;
import stocktales.NFS.model.config.NFSConfig;
import stocktales.durations.UtilDurations;
import stocktales.historicalPrices.utility.StockPricesUtility;
import stocktales.maths.UtilPercentages;
import stocktales.money.UtilDecimaltoMoneyString;
import yahoofinance.Stock;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Service()
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class IDS_PFDashBoardUISrv implements stocktales.IDS.srv.intf.IDS_PFDashBoardUISrv
{

	/**
	 * ----------------------- AUTOWIRED SECTION STARTS --------
	 */
	@Autowired
	private RepoHC repoHC;

	@Autowired
	private RepoHCI repoHCI;

	@Autowired
	private IDS_CorePFSrv corePFSrv;

	@Autowired
	private RepoPFSchema repoPFSchema;

	@Autowired
	private IDS_PFSchema_REbalUI_Srv schRebalSrv;

	@Autowired
	private NFSConfig nfsConfig;

	/**
	 * ----------------------- AUTOWIRED SECTION ENDS ----------
	 */

	private PFDBContainer pfDBCtr;

	/**
	 * ----------------------- IMPLEMENTATION SECTION STARTS --------
	 */

	@Override
	public PFDBContainer getPFDashBoardContainer() throws Exception
	{
		if (pfDBCtr == null)
		{
			pfDBCtr = new PFDBContainer();
			initialize();
		} else
		{
			// Always REfresh the Basic Portion
			this.refreshContainer4RoundTrip();
		}

		return this.pfDBCtr;
	}

	@Override
	public PFDBContainer getPFDashBoardContainer4mSession() throws Exception
	{

		return this.pfDBCtr;
	}

	@Override
	public void refreshContainer4SchemaChange() throws Exception
	{
		if (this.pfDBCtr != null)
		{
			if (repoPFSchema.count() > 0)
			{
				this.pfDBCtr.setSchemaDetails(repoPFSchema.findAll());
				if (this.schRebalSrv != null) // Only if Null
				{
					if (this.schRebalSrv.getRebalContainer() == null)
					{
						pfDBCtr.setSchemaStats(this.schRebalSrv.uploadSchemaforUpdate().getStats());
					}
				}

				/*
				 * LOAD Buy Proposals
				 */
				IDS_BuyProposalBO buyP = corePFSrv.getBuyProposals();
				if (buyP != null)
				{
					this.pfDBCtr.setBuyProposals(buyP);
				}

				/*
				 * Load SMA Preview
				 */
				this.pfDBCtr.setSmaPvwL(corePFSrv.getPFSchemaSMAPreview());

				/*
				 * Build Holdings P&L Table
				 */
				buildHoldingsPLTable();

			}
		}
	}

	@Override
	public void refreshContainer4RoundTrip() throws Exception
	{
		if (this.pfDBCtr != null)
		{
			// Set Holdings P&L List
			this.pfDBCtr.getHoldings().clear();
			buildHoldingsPLTable();

			// Build Stats Header
			this.pfDBCtr.setStatsH(new PFStatsH());
			buildStatsHeader();

			// Buy Proposals
			IDS_BuyProposalBO buyPBO = corePFSrv.getBuyProposals();
			this.pfDBCtr.setBuyProposals(buyPBO);

			// SMA PVW No need to change on Each REfresh - Anyways computed for Buy
			// Proposals each time

			// Schema Details No need to change on each refresh

			// Update XIRR for PF
			this.pfDBCtr.setXirrContainer(corePFSrv.calculateXIRRforPF());

		}

	}

	@Override
	public void refreshContainer4RoundTrip(IDS_BuyProposalBO buyPBO) throws Exception
	{
		if (this.pfDBCtr != null)
		{
			// Set Holdings P&L List
			this.pfDBCtr.getHoldings().clear();
			buildHoldingsPLTable();

			// Build Stats Header
			this.pfDBCtr.setStatsH(new PFStatsH());
			buildStatsHeader();

			// Buy Proposals
			this.pfDBCtr.setBuyProposals(buyPBO);

			// SMA PVW No need to change on Each REfresh - Anyways computed for Buy
			// Proposals each time

			// Schema Details No need to change on each refresh

			// Update XIRR for PF
			this.pfDBCtr.setXirrContainer(corePFSrv.calculateXIRRforPF());
		}

	}

	@Override
	public void refreshContainer4Txn() throws Exception
	{
		if (this.pfDBCtr != null)
		{
			refreshContainer4RoundTrip();
			this.pfDBCtr.setSchemaDetails(repoPFSchema.findAll());
		}

	}

	/**
	 * -------------------------------------------------------------------
	 * ----------- PRIVATE METHODS ---------------------------------------
	 * -------------------------------------------------------------------
	 */
	private void initialize() throws Exception
	{

		if (this.corePFSrv != null)
		{

			/*
			 * LOAD PFSchema
			 */

			if (repoPFSchema.count() > 0)
			{
				this.pfDBCtr.setSchemaDetails(repoPFSchema.findAll());
				if (this.schRebalSrv != null) // Only if Null
				{
					if (this.schRebalSrv.getRebalContainer() == null)
					{
						pfDBCtr.setSchemaStats(this.schRebalSrv.uploadSchemaforUpdate().getStats());
					}
				}

				/*
				 * LOAD Buy Proposals
				 */
				IDS_BuyProposalBO buyP = corePFSrv.getBuyProposals();
				if (buyP != null)
				{
					this.pfDBCtr.setBuyProposals(buyP);
				}

				/*
				 * Load SMA Preview
				 */
				this.pfDBCtr.setSmaPvwL(corePFSrv.getPFSchemaSMAPreview());

				/*
				 * Build Holdings P&L Table
				 */
				buildHoldingsPLTable();

				/*
				 * Build Stats Header
				 */
				buildStatsHeader();

				// Update XIRR for PF
				this.pfDBCtr.setXirrContainer(corePFSrv.calculateXIRRforPF());
			}

		}
	}

	private void buildHoldingsPLTable() throws Exception
	{
		if (repoHC != null)
		{
			List<HC> pfHoldings = repoHC.findAll();
			if (pfHoldings.size() > 0)
			{
				for (HC hc : pfHoldings)
				{

					PFHoldingsPL pfPL_H = new PFHoldingsPL();
					pfPL_H.setScCode(hc.getSccode());
					Optional<PFSchema> pfSChO = this.pfDBCtr.getSchemaDetails().stream()
							.filter(x -> x.getSccode().equals(hc.getSccode())).findFirst();
					if (pfSChO.isPresent())
					{
						pfPL_H.setSector(pfSChO.get().getSector());
						pfPL_H.setDepAmnt(Precision.round(pfSChO.get().getDepamnt(), 0));
						pfPL_H.setDepAmntStr(
								UtilDecimaltoMoneyString.getMoneyStringforDecimal(pfSChO.get().getDepamnt(), 2));
						double inv = hc.getUnits() * hc.getPpu();
						pfPL_H.setInvestments(Precision.round(inv, 1));
						pfPL_H.setInvString(UtilDecimaltoMoneyString.getMoneyStringforDecimal(inv, 2));
						double total = inv + pfSChO.get().getDepamnt();
						pfPL_H.setDepPer(Precision.round((pfSChO.get().getDepamnt() * 100 / total), 1));
					}

					pfPL_H.setUnits(hc.getUnits());
					pfPL_H.setPpu(hc.getPpu());

					Stock quote = StockPricesUtility.getQuoteforScrip(hc.getSccode());
					if (quote != null)
					{
						pfPL_H.setCmp(Precision.round(quote.getQuote().getPrice().doubleValue(), 1));
						pfPL_H.setCurrVal(Precision.round(pfPL_H.getCmp() * pfPL_H.getUnits(), 0));
						pfPL_H.setCurrValString(
								UtilDecimaltoMoneyString.getMoneyStringforDecimal(pfPL_H.getCurrVal(), 2));
						if (quote.getStats().getMarketCap() != null)
						{
							double Mcap = Precision.round((quote.getStats().getMarketCap().doubleValue() / 10000000),
									0);
							pfPL_H.setMCap(UtilDecimaltoMoneyString.getMoneyStringforDecimal(Mcap, 1));

							EnumMCapClassification mcapName = nfsConfig.getMcapClassificationForMCapKCr(Mcap);
							pfPL_H.setMCapClass(mcapName);

						}

						double pl = hc.getUnits() * (pfPL_H.getCmp() - hc.getPpu());
						double plPer = (pl / (hc.getUnits() * hc.getPpu())) * 100;

						pfPL_H.setPl(Precision.round(pl, 2));
						pfPL_H.setPlPer(Precision.round(plPer, 1));

						pfPL_H.setPlStr(UtilDecimaltoMoneyString.getMoneyStringforDecimal(pfPL_H.getPl(), 2));

						pfPL_H.setDayPLPer(
								UtilPercentages.getPercentageDelta(quote.getQuote().getPreviousClose().doubleValue(),
										quote.getQuote().getPrice().doubleValue(), 1));

						pfPL_H.setDayPL(
								Precision.round((pfPL_H.getDayPLPer() * (hc.getUnits() * hc.getPpu()) / 100), 0));

						/**
						 * SMA Classification v/s CMP
						 */

						Optional<IDS_SMAPreview> smaO = this.pfDBCtr.getSmaPvwL().stream()
								.filter(w -> w.getScCode().equals(hc.getSccode())).findFirst();
						if (smaO.isPresent())
						{
							IDS_SMAPreview smaEnt1 = smaO.get();
							IDS_SMAPreview smaEnt = SMASortUtility.getSMASortedforIDS(smaEnt1);
							if (smaEnt != null)
							{
								if (pfPL_H.getCmp() < smaEnt.getSMAI1())
								{
									pfPL_H.setSmaLvl(EnumSMABreach.sma1);
								}
								if (pfPL_H.getCmp() < smaEnt.getSMAI2())
								{
									pfPL_H.setSmaLvl(EnumSMABreach.sma2);
								}
								if (pfPL_H.getCmp() < smaEnt.getSMAI3())
								{
									pfPL_H.setSmaLvl(EnumSMABreach.sma3);
								}
								if (pfPL_H.getCmp() < smaEnt.getSMAI4())
								{
									pfPL_H.setSmaLvl(EnumSMABreach.sma4);
								}
							}

						}

					}

					try
					{

						pfPL_H.setLastBuyDate(repoHCI.getlastBuyTxnDateforScrip(hc.getSccode()));

						pfPL_H.setNumDaysBuy(UtilDurations.getNumDaysbwSysDates(pfPL_H.getLastBuyDate(),
								UtilDurations.getTodaysDate()));

					} catch (Exception e)
					{
						// TODO: handle exception
					}

					try
					{
						Date sellDate = repoHCI.getlastSellTxnDateforScrip(hc.getSccode());
						pfPL_H.setLastSellDate(sellDate);

					} catch (Exception e)
					{
						// TODO: handle exception
					}

					this.pfDBCtr.getHoldings().add(pfPL_H);

				}
			}
		}

	}

	private void buildStatsHeader()
	{

		if (repoHC.count() > 0)
		{
			pfDBCtr.getStatsH().setTotalInv(repoHC.getTotalInvestments());
			pfDBCtr.getStatsH().setTotalInvStr(
					UtilDecimaltoMoneyString.getMoneyStringforDecimal(pfDBCtr.getStatsH().getTotalInv(), 2));

			double totalDepAmnt = repoPFSchema.getSumDeploymentAmount();

			double utlzPer = 100 - ((totalDepAmnt / (pfDBCtr.getStatsH().getTotalInv() + totalDepAmnt)) * 100);
			pfDBCtr.getStatsH().setAmntUtilPer(Precision.round(utlzPer, 1));

			double pl = pfDBCtr.getHoldings().stream().mapToDouble(PFHoldingsPL::getPl).sum();
			pfDBCtr.getStatsH().getPfPLSS().setAmountPL(pl);
			pfDBCtr.getStatsH().getPfPLSS().setAmountPLStr(UtilDecimaltoMoneyString.getMoneyStringforDecimal(pl, 1));
			pfDBCtr.getStatsH().getPfPLSS()
					.setPerPL(Precision.round(((pl * 100) / pfDBCtr.getStatsH().getTotalInv()), 1));
			pfDBCtr.getStatsH()
					.setCurrVal(pfDBCtr.getStatsH().getTotalInv() + pfDBCtr.getStatsH().getPfPLSS().getAmountPL());

			pfDBCtr.getStatsH().setCurrValStr(
					UtilDecimaltoMoneyString.getMoneyStringforDecimal(pfDBCtr.getStatsH().getCurrVal(), 2));

			int pfPos = (int) pfDBCtr.getHoldings().stream().filter(s -> s.getPl() > 0).count();
			int pfLos = pfDBCtr.getHoldings().size() - pfPos;

			pfDBCtr.getStatsH().getPfPLSS().setNumGainers(pfPos);
			pfDBCtr.getStatsH().getPfPLSS().setNumLosers(pfLos);

			double dayPL = pfDBCtr.getHoldings().stream().mapToDouble(PFHoldingsPL::getDayPL).sum();
			pfDBCtr.getStatsH().getTodayPLSS().setAmountPL(dayPL);
			pfDBCtr.getStatsH().getTodayPLSS()
					.setAmountPLStr(UtilDecimaltoMoneyString.getMoneyStringforDecimal(dayPL, 1));
			int dayPos = (int) pfDBCtr.getHoldings().stream().filter(c -> c.getDayPL() > 0).count();
			int dayLos = pfDBCtr.getHoldings().size() - dayPos;
			pfDBCtr.getStatsH().getTodayPLSS().setNumGainers(dayPos);
			pfDBCtr.getStatsH().getTodayPLSS().setNumLosers(dayLos);

			double ydayValue = (pfDBCtr.getStatsH().getTotalInv() + pfDBCtr.getStatsH().getPfPLSS().getAmountPL())
					- dayPL;
			double daysGainPer = (dayPL / ydayValue) * 100;
			pfDBCtr.getStatsH().getTodayPLSS().setPerPL((Precision.round(daysGainPer, 1)));

			// Get Max Gainer
			PFHoldingsPL maxGainer = Collections.max(pfDBCtr.getHoldings(), Comparator.comparing(x -> x.getPlPer()));
			if (maxGainer != null)
			{
				pfDBCtr.getStatsH().setMaxGainer(new ScripPLSS(maxGainer.getScCode(), maxGainer.getPl(),
						maxGainer.getPlStr(), maxGainer.getPlPer()));
			}

			// Get Max Loser
			PFHoldingsPL maxLoser = Collections.min(pfDBCtr.getHoldings(), Comparator.comparing(x -> x.getPlPer()));
			if (maxGainer != null)
			{
				pfDBCtr.getStatsH().setMaxLoser(new ScripPLSS(maxLoser.getScCode(), maxLoser.getPl(),
						maxLoser.getPlStr(), maxLoser.getPlPer()));
			}

			// Populate PF Start Date
			pfDBCtr.getStatsH().setInvSince(repoHCI.getPFInvSinceDate());
			SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
			pfDBCtr.getStatsH().setInvSinceStr(formatter.format(pfDBCtr.getStatsH().getInvSince()));
		}

	}

}
