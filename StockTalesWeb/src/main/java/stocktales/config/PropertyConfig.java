package stocktales.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;

import stocktales.NFS.model.config.NFSConfig;
import stocktales.basket.allocations.config.pojos.AllocationWeights;
import stocktales.basket.allocations.config.pojos.DurationWeights;
import stocktales.basket.allocations.config.pojos.FinancialSectors;
import stocktales.basket.allocations.config.pojos.FinancialsConfig;
import stocktales.basket.allocations.config.pojos.MCapAllocations;
import stocktales.basket.allocations.config.pojos.SCPricesMode;
import stocktales.basket.allocations.config.pojos.StrengthWeights;
import stocktales.basket.allocations.config.pojos.Urls;

/*
 * ------------------ PROPERTY FILES BASED BEANS -----------------------
 */

@Configuration
@PropertySources(
{ @PropertySource("classpath:weights.properties"), @PropertySource("classpath:application.properties"),
		@PropertySource("classpath:messages.properties"), @PropertySource("classpath:HCMessages.properties"),
		@PropertySource("classpath:HCRep.properties"), @PropertySource("classpath:nfs.properties"),
		@PropertySource("classpath:HCRep.properties"), @PropertySource("classpath:url.properties") })
public class PropertyConfig
{

	@Bean
	public static PropertySourcesPlaceholderConfigurer properties()
	{
		PropertySourcesPlaceholderConfigurer pSConf = new PropertySourcesPlaceholderConfigurer();
		return pSConf;
	}

	@Bean
	@Autowired // For PropertySourcesPlaceholderConfigurer
	public DurationWeights durationWeightsConfig(@Value("${DurationWeights.wt3Y}") final double wt3Y,
			@Value("${DurationWeights.wt5Y}") final double wt5Y, @Value("${DurationWeights.wt7Y}") final double wt7Y,
			@Value("${DurationWeights.wt10Y}") final double wt10Y)

	{
		DurationWeights duWts = new DurationWeights(wt3Y, wt5Y, wt7Y, wt10Y);

		return duWts;
	}

	@Bean
	@Autowired // For PropertySourcesPlaceholderConfigurer
	public AllocationWeights allocationWeightsConfig(@Value("${AllocationWeights.wtED}") final double wtED,
			@Value("${AllocationWeights.wtRR}") final double wtRR,
			@Value("${AllocationWeights.wtCF}") final double wtCF)

	{
		AllocationWeights allocWts = new AllocationWeights(wtED, wtRR, wtCF);

		return allocWts;
	}

	@Bean
	@Autowired // For PropertySourcesPlaceholderConfigurer
	public Urls urlsConfig(@Value("${coreScUrl}") String coreScUl)

	{
		Urls urls = new Urls(coreScUl);

		return urls;
	}

	@Bean
	@Autowired // For PropertySourcesPlaceholderConfigurer
	public SCPricesMode setPriceModeBean(@Value("${scpricesDBMode}") int scpriceMode)

	{
		SCPricesMode scMode = new SCPricesMode(scpriceMode);

		return scMode;
	}

	@Bean
	@Autowired // For PropertySourcesPlaceholderConfigurer
	public FinancialSectors financialSectorsConfig(@Value("${SectorName}") String sectorName)

	{
		FinancialSectors finSec = new FinancialSectors(sectorName);

		return finSec;
	}

	@Bean
	@Autowired // For PropertySourcesPlaceholderConfigurer
	public StrengthWeights strenghtWeightsConfig(@Value("${StrengthWeights.EDRC}") final double EDRC,
			@Value("${StrengthWeights.ValR}") final double valR)
	{
		StrengthWeights strengthWts = new StrengthWeights(EDRC, valR);

		return strengthWts;
	}

	@Bean
	@Autowired // For PropertySourcesPlaceholderConfigurer
	public FinancialsConfig setFinConfig(@Value("${Financials.UPH}") final double UPH,
			@Value("${Financials.ROE}") final double ROE, @Value("${Financials.BOOSTBest}") final double BoostBest,
			@Value("${Financials.BOOSTROE}") final double BoostROE,
			@Value("${Financials.BOOSTBASE}") final double BoostBase)
	{
		FinancialsConfig FinConfig = new FinancialsConfig(UPH, ROE, BoostBest, BoostROE, BoostBase);

		return FinConfig;
	}

	@Bean
	@Autowired // For PropertySourcesPlaceholderConfigurer
	public MCapAllocations MCapAllocationsConfig(@Value("${MCapValue}") final double MCap,
			@Value("${MaxAllocLimit}") final double allocMax)
	{
		MCapAllocations mCapAlloc = new MCapAllocations(MCap, allocMax);

		return mCapAlloc;
	}

	@Bean
	@Autowired // For PropertySourcesPlaceholderConfigurer
	public NFSConfig NFSConfigLoad(@Value("${MonthsMinTrade}") final int months,
			@Value("${MinMcapCr}") final double mcap, @Value("${TopDataSetPercentRR}") final double dsPer,
			@Value("${RRThresholdEmerging}") final double rrAllow,
			@Value("${SMA20DeltaInclPercent}") final double sma20Delta,
			@Value("${SMA100DeltaInclPercent}") final double sma100Delta, @Value("${WtMR}") final double wtMR,
			@Value("${Wt20SMADelta}") final double sma20Wt, @Value("${Wt100SMADelta}") final double sma100Wt,
			@Value("${T2TMaxPerPF}") final double t2tMaxPer, @Value("${PFSize}") final int pfSize,
			@Value("${screenerpf}") final String screenerPfx, @Value("${screenersf}") final String screenerSfx,
			@Value("${mSmallCap}") final double smallCapMCap, @Value("${mLargeCap}") final double largeCapMCap,
			@Value("${T2TSeries}") final String t2tSeriesName, @Value("${maxPFlotSize}") final double maxpfLotSize,
			@Value("${smaNFSExitCompare}") final int smadays, @Value("${NFSSlotMax}") final int nfsSlotMax,
			@Value("${smaNFSExitRankFailCompare}") final int nfsSlotMaxRankFail,
			@Value("${topGunPFSize}") final int topGunPFScrips, @Value("${topGunPFSlotMax}") final int topGunPFSlotMax)
	{
		NFSConfig nfsConfig = new NFSConfig(months, mcap, dsPer, rrAllow, sma20Delta, sma100Delta, sma20Wt, sma100Wt,
				wtMR, t2tMaxPer, pfSize, screenerPfx, screenerSfx, smallCapMCap, largeCapMCap, t2tSeriesName,
				maxpfLotSize, smadays, nfsSlotMax, nfsSlotMaxRankFail, topGunPFScrips, topGunPFSlotMax);

		return nfsConfig;
	}

	@Bean
	public ResourceBundleMessageSource messageSource()
	{

		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.addBasenames("messages");
		source.addBasenames("HCMessages");
		source.addBasenames("HCRep");
		source.addBasenames("HCTags");
		source.setUseCodeAsDefaultMessage(true);

		return source;
	}

}
