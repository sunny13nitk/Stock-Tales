package stocktales.basket.allocations.autoAllocation.facades.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import stocktales.basket.allocations.autoAllocation.facades.interfaces.EDRCFacade;
import stocktales.basket.allocations.autoAllocation.facades.pojos.SC_EDRC_Summary;
import stocktales.basket.allocations.autoAllocation.facades.pojos.SC_EDRC_Summary_List_Repo;
import stocktales.basket.allocations.autoAllocation.interfaces.EDRCScoreCalcSrv;
import stocktales.basket.allocations.autoAllocation.interfaces.ISrv_FCFSCore;
import stocktales.basket.allocations.autoAllocation.pojos.FCFScore;
import stocktales.basket.allocations.autoAllocation.pojos.SCCalibrationItem;
import stocktales.basket.allocations.autoAllocation.pojos.ScripEDRCScore;
import stocktales.basket.allocations.config.pojos.CalibrationItem;
import stocktales.basket.allocations.config.pojos.FinancialsConfig;
import stocktales.basket.allocations.config.pojos.StrengthWeights;
import stocktales.basket.allocations.config.services.CLRConfig;
import stocktales.enums.SCSScoreCalibrationHeaders;
import stocktales.helperPOJO.NameValDouble;
import stocktales.repository.SC10YearRepository;
import stocktales.repository.TrendsRepository;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_10YData;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_Trends;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.ISCExistsDB_Srv;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.types.ScripSector;
import stocktales.services.interfaces.ScripService;
import stocktales.utilities.MathUtilities;

@Service
public class EDRCFacadeImpl implements EDRCFacade
{
	@Autowired
	private EDRCScoreCalcSrv edrcSrv;

	@Autowired
	private ScripService scSrv;

	@Autowired
	private SC10YearRepository sc10Yrepo;

	@Autowired
	private ISCExistsDB_Srv scExSrv;

	@Autowired
	private StrengthWeights strWts;

	@Autowired
	private CLRConfig clrConfig;

	@Autowired
	private TrendsRepository repoTrends;

	@Autowired
	private SC10YearRepository repo10Yr;

	@Autowired
	private FinancialsConfig cfFinancials;

	@Autowired
	private SC_EDRC_Summary_List_Repo edrcFilteredRepo;

	@Autowired
	private ISrv_FCFSCore srv_FCFScore;

	@Value("${trendValPeriod}")
	private String trendValPeriod;

	private List<ScripSector> scripSectors;

	@Value("${valuationSoothingSS}")
	private boolean isValuationSoothingEnabled;

	@Override
	public List<SC_EDRC_Summary> getEDRCforSCripsList(List<String> scrips)
	{
		List<SC_EDRC_Summary> result = null;
		if (scrips != null)
		{
			if (scrips.size() > 0 && edrcSrv != null)
			{
				result = new ArrayList<SC_EDRC_Summary>();

				for (String scrip : scrips)
				{

					result.add(this.getEDRCforSCrip(scrip));
				}
			}
		}

		return result;
	}

	@Override
	public List<NameValDouble> getTopNED(int numScrips)
	{
		List<ScripEDRCScore> edAllSc = null;
		List<NameValDouble> scEdAll = new ArrayList<NameValDouble>();
		List<NameValDouble> edSorted = null;
		List<NameValDouble> topN = null;
		try
		{
			List<String> scrips = scExSrv.getAllScripNames();
			if (scrips != null)
			{
				for (String string : scrips)
				{
					ScripEDRCScore edrcScore = edrcSrv.getEDRCforScrip(string);
					scEdAll.add(
							new NameValDouble(edrcScore.getScCode(), edrcScore.getEarningsDivScore().getResValue()));
				}

				edSorted = scEdAll.stream().sorted(Comparator.comparingDouble(NameValDouble::getValue).reversed())
						.collect(Collectors.toList());
				topN = edSorted.stream().limit(numScrips).collect(Collectors.toList());

			}

		} catch (EX_General e)
		{

			e.printStackTrace();
		}

		return topN;
	}

	private void loadScripsandSectors() throws EX_General
	{

		this.scripSectors = scExSrv.getAllScripSectors();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SC_EDRC_Summary> getEDRCforSCripsList(List<String> scrips,
			Predicate<? extends SC_EDRC_Summary> predicate)
	{
		List<SC_EDRC_Summary> result = null;
		List<SC_EDRC_Summary> filteredR = null;
		if (predicate != null && scrips != null)
		{
			result = this.getEDRCforSCripsList(scrips);
		}
		if (result != null)
		{
			filteredR = result.stream().filter((Predicate<? super SC_EDRC_Summary>) predicate)
					.collect(Collectors.toList());
			if (filteredR != null)
			{
				if (filteredR.size() > 0)
				{
					edrcFilteredRepo.setEdrcFilteredList(filteredR);
				}
			}

		}
		// TODO Auto-generated method stub
		return filteredR;
	}

	@Override
	public SC_EDRC_Summary getEDRCforSCrip(String scrip)
	{
		SC_EDRC_Summary summary = null;

		ScripEDRCScore scEDRC = edrcSrv.getEDRCforScrip(scrip);
		String sector = null;
		if (scripSectors == null)
		{
			try
			{
				loadScripsandSectors();
			} catch (EX_General e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		sector = scripSectors.stream().filter(x -> x.getScCode().equals(scrip)).findFirst().get().getSector();

		if (scEDRC != null)
		{
			double strengthWt = Precision.round((sc10Yrepo.findBySCCode(scrip).get().getValR() * strWts.getValR()
					+ scEDRC.getEdrcScore() * strWts.getEDRC()), 1);

			FCFScore fcfscore = new FCFScore(scrip, 0, 0);
			if (srv_FCFScore != null)
			{
				fcfscore = srv_FCFScore.getFCFScorebyScrip(scrip);
			}

			if (scEDRC.getCashflowsScore() != null)
			{
				summary = new SC_EDRC_Summary(scrip, sector, scEDRC,
						Precision.round(scEDRC.getEarningsDivScore().getResValue(), 1),
						Precision.round(scEDRC.getReturnRatiosScore().getNettValue(), 1),
						Precision.round(scEDRC.getCashflowsScore().getNettValue(), 1),
						Precision.round(fcfscore.getFcfYield(), 1), Precision.round(fcfscore.getCfoYield(), 1),
						Precision.round(scEDRC.getEdrcScore(), 1),
						Precision.round(sc10Yrepo.findBySCCode(scrip).get().getValR(), 1),
						Precision.round(sc10Yrepo.findBySCCode(scrip).get().getCFOPATR(), 1), strengthWt, strengthWt,
						new ArrayList<SCCalibrationItem>());
			} else
			{
				summary = new SC_EDRC_Summary(scrip, sector, scEDRC,
						Precision.round(scEDRC.getEarningsDivScore().getResValue(), 1),
						Precision.round(scEDRC.getReturnRatiosScore().getNettValue(), 1), 0, 0, 0,
						Precision.round(scEDRC.getEdrcScore(), 1),
						Precision.round(sc10Yrepo.findBySCCode(scrip).get().getValR(), 1),
						Precision.round(sc10Yrepo.findBySCCode(scrip).get().getCFOPATR(), 1), strengthWt, strengthWt,
						new ArrayList<SCCalibrationItem>());
			}
		}

		/*
		 * If Valuation Soothing of Strength Score is Active - Sooth out the scores
		 */

		if (isValuationSoothingEnabled)
		{
			// Scan for Right Valuation Zone
			double cvalR = summary.getValR();
			Optional<CalibrationItem> currValConfig = this.clrConfig.getValSoothConfigL().stream()
					.filter(x -> x.getMinm() <= cvalR && x.getMaxm() >= cvalR).findFirst();
			if (currValConfig.isPresent())
			{
				SCCalibrationItem calItem = new SCCalibrationItem();
				calItem.setCalHeader(SCSScoreCalibrationHeaders.ValueRatioCalibration);
				calItem.setTriggerVal(Precision.round(cvalR, 1));
				calItem.setValB4(summary.getStrengthScore());
				summary.setStrengthScore(
						Precision.round((summary.getStrengthScore() * currValConfig.get().getSf()), 1));
				calItem.setValAfter(summary.getStrengthScore());
				calItem.setDelta(MathUtilities.getPercentageDelta(calItem.getValB4(), calItem.getValAfter()));
				summary.getCalibrations().add(calItem);
			}

		}

		/*
		 * Re-calibrate for Un-pledged Promoter Holding, Working Capital Cycle,
		 * Financial Strength (Interest + Dep.) /PAT - Only for Non financial Scrips
		 */

		if (!scSrv.isScripBelongingToFinancialSector(scrip))

		{

			// UPH SS Calibration
			if (clrConfig.getUPHConfigL() != null)
			{
				try
				{
					double UPH = scExSrv.Get_ScripExisting_DB(scrip).getUPH();

					Optional<CalibrationItem> currUPHConfig = this.clrConfig.getUPHConfigL().stream()
							.filter(x -> x.getMinm() <= UPH && x.getMaxm() >= UPH).findFirst();
					if (currUPHConfig.isPresent())
					{
						SCCalibrationItem calItem = new SCCalibrationItem();
						calItem.setCalHeader(SCSScoreCalibrationHeaders.UnpledgedPromoterHoldingCalibration);
						calItem.setTriggerVal(Precision.round(UPH, 1));
						calItem.setValB4(summary.getStrengthScore());

						summary.setStrengthScore(
								Precision.round((summary.getStrengthScore() * currUPHConfig.get().getSf()), 1));

						calItem.setValAfter(summary.getStrengthScore());
						calItem.setDelta(MathUtilities.getPercentageDelta(calItem.getValB4(), calItem.getValAfter()));
						summary.getCalibrations().add(calItem);

					}
				} catch (EX_General e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			// WC Cycle Calibration
			if (clrConfig.getWCConfigL() != null)
			{

				Optional<EN_SC_Trends> trendEntO = repoTrends.findBySCCodeAndPeriod(scrip, trendValPeriod);
				if (trendEntO.isPresent())
				{
					EN_SC_Trends trendEnt = trendEntO.get();
					double WCCycle = trendEnt.getWCCAvg();

					Optional<CalibrationItem> currWCConfig = this.clrConfig.getWCConfigL().stream()
							.filter(x -> x.getMinm() <= WCCycle && x.getMaxm() >= WCCycle).findFirst();
					if (currWCConfig.isPresent())
					{
						SCCalibrationItem calItem = new SCCalibrationItem();
						calItem.setCalHeader(SCSScoreCalibrationHeaders.WorkingCapitalCycleCalibration);
						calItem.setTriggerVal(Precision.round(WCCycle, 1));
						calItem.setValB4(summary.getStrengthScore());
						summary.setStrengthScore(
								Precision.round((summary.getStrengthScore() * currWCConfig.get().getSf()), 1));

						calItem.setValAfter(summary.getStrengthScore());
						calItem.setDelta(MathUtilities.getPercentageDelta(calItem.getValB4(), calItem.getValAfter()));
						summary.getCalibrations().add(calItem);

					}
				}

			}

			// IDBP Cycle Calibration
			if (clrConfig.getIDBPConfigL() != null)
			{

				Optional<EN_SC_Trends> trendEntO = repoTrends.findBySCCodeAndPeriod(scrip, trendValPeriod);
				if (trendEntO.isPresent())
				{
					EN_SC_Trends trendEnt = trendEntO.get();
					double IDBP = trendEnt.getFViabAvg();

					Optional<CalibrationItem> currIDBPConfig = this.clrConfig.getIDBPConfigL().stream()
							.filter(x -> x.getMinm() <= IDBP && x.getMaxm() >= IDBP).findFirst();
					if (currIDBPConfig.isPresent())
					{
						SCCalibrationItem calItem = new SCCalibrationItem();
						calItem.setCalHeader(SCSScoreCalibrationHeaders.InterestDepriciationByPATCalibration);
						calItem.setTriggerVal(Precision.round(IDBP, 1));
						calItem.setValB4(summary.getStrengthScore());

						summary.setStrengthScore(
								Precision.round((summary.getStrengthScore() * currIDBPConfig.get().getSf()), 1));

						calItem.setValAfter(summary.getStrengthScore());
						calItem.setDelta(MathUtilities.getPercentageDelta(calItem.getValB4(), calItem.getValAfter()));
						summary.getCalibrations().add(calItem);
					}
				}

			}

			// CFO/PAT Calibration
			if (clrConfig.getCFOPATConfigL() != null)
			{

				Optional<EN_SC_10YData> sc10YEntO = repo10Yr.findBySCCode(scrip);
				if (sc10YEntO.isPresent())
				{
					EN_SC_10YData sc10YEnt = sc10YEntO.get();

					double CFOPATAdh = sc10YEnt.getCFOPATR();

					Optional<CalibrationItem> currCFOPATConfig = this.clrConfig.getCFOPATConfigL().stream()
							.filter(x -> x.getMinm() <= CFOPATAdh && x.getMaxm() >= CFOPATAdh).findFirst();
					if (currCFOPATConfig.isPresent())
					{
						SCCalibrationItem calItem = new SCCalibrationItem();
						calItem.setCalHeader(SCSScoreCalibrationHeaders.CFOPATAdherenceCalibration);
						calItem.setTriggerVal(Precision.round(CFOPATAdh, 1));
						calItem.setValB4(summary.getStrengthScore());

						summary.setStrengthScore(
								Precision.round((summary.getStrengthScore() * currCFOPATConfig.get().getSf()), 1));

						calItem.setValAfter(summary.getStrengthScore());
						calItem.setDelta(MathUtilities.getPercentageDelta(calItem.getValB4(), calItem.getValAfter()));
						summary.getCalibrations().add(calItem);
					}
				}

			}

			// FCF Yield Calibration
			if (clrConfig.getFCFYieldConfigL() != null)
			{

				double fcfYield = srv_FCFScore.getFCFYield(scrip);
				if (fcfYield != 0)
				{

					Optional<CalibrationItem> currFCFYieldConfig = this.clrConfig.getFCFYieldConfigL().stream()
							.filter(x -> x.getMinm() <= fcfYield && x.getMaxm() >= fcfYield).findFirst();
					if (currFCFYieldConfig.isPresent())
					{
						SCCalibrationItem calItem = new SCCalibrationItem();
						calItem.setCalHeader(SCSScoreCalibrationHeaders.FCFYieldCalibration);
						calItem.setTriggerVal(Precision.round(fcfYield, 1));
						calItem.setValB4(summary.getStrengthScore());

						summary.setStrengthScore(
								Precision.round((summary.getStrengthScore() * currFCFYieldConfig.get().getSf()), 1));

						calItem.setValAfter(summary.getStrengthScore());
						calItem.setDelta(MathUtilities.getPercentageDelta(calItem.getValB4(), calItem.getValAfter()));
						summary.getCalibrations().add(calItem);
					}
				}

			}

			// CFO Yield Calibration
			if (clrConfig.getCFOYieldConfigL() != null)
			{

				double cfoYield = srv_FCFScore.getCFOYield(scrip);
				if (cfoYield != 0)
				{

					Optional<CalibrationItem> currCFOYieldConfig = this.clrConfig.getCFOYieldConfigL().stream()
							.filter(x -> x.getMinm() <= cfoYield && x.getMaxm() >= cfoYield).findFirst();
					if (currCFOYieldConfig.isPresent())
					{
						SCCalibrationItem calItem = new SCCalibrationItem();
						calItem.setCalHeader(SCSScoreCalibrationHeaders.CFOYieldCalibration);
						calItem.setTriggerVal(Precision.round(cfoYield, 1));
						calItem.setValB4(summary.getStrengthScore());

						summary.setStrengthScore(
								Precision.round((summary.getStrengthScore() * currCFOYieldConfig.get().getSf()), 1));

						calItem.setValAfter(summary.getStrengthScore());
						calItem.setDelta(MathUtilities.getPercentageDelta(calItem.getValB4(), calItem.getValAfter()));
						summary.getCalibrations().add(calItem);
					}
				}

			}

		}

		else // Adjust Financial Scrips FOR WC + UPH + CFP/PAT adherence to a tune of 8 + 8
				// +3 = 19% for a level playfield
		{
			try
			{
				SCCalibrationItem calItem = new SCCalibrationItem();
				calItem.setCalHeader(SCSScoreCalibrationHeaders.FinancialSectorBaseBooster);
				calItem.setTriggerVal(Precision.round(0, 1));
				calItem.setValB4(summary.getStrengthScore());

				double booster = 1 + cfFinancials.getBoostBase();
				double UPH = scExSrv.Get_ScripExisting_DB(scrip).getUPH();
				if (UPH >= cfFinancials.getUPH() && summary.getAvWtRR() >= cfFinancials.getROE())
				{
					calItem.setCalHeader(SCSScoreCalibrationHeaders.FinancialSectorBestBooster);
					calItem.setTriggerVal(Precision.round(UPH, 1));
					calItem.setValB4(summary.getStrengthScore());
					booster = 1 + cfFinancials.getBoostBest();
				} else
				{
					// check for ROE
					if (summary.getAvWtRR() >= cfFinancials.getROE())
					{
						calItem.setCalHeader(SCSScoreCalibrationHeaders.FinancialSectorROEBooster);
						calItem.setTriggerVal(Precision.round(summary.getAvWtRR(), 1));
						calItem.setValB4(summary.getStrengthScore());
						booster = 1 + cfFinancials.getBoostROE();
					}
				}

				summary.setStrengthScore(Precision.round((summary.getStrengthScore() * booster), 1));

				calItem.setValAfter(summary.getStrengthScore());
				calItem.setDelta(MathUtilities.getPercentageDelta(calItem.getValB4(), calItem.getValAfter()));
				summary.getCalibrations().add(calItem);

			} catch (EX_General e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return summary;
	}

}
