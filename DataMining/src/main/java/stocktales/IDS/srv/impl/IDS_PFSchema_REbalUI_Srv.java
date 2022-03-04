package stocktales.IDS.srv.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.IDS.enums.EnumMode;
import stocktales.IDS.model.pf.entity.PFSchema;
import stocktales.IDS.model.pf.repo.RepoPFSchema;
import stocktales.IDS.pojo.IDS_SCAlloc;
import stocktales.IDS.pojo.IDS_ScAllocMassUpdate;
import stocktales.IDS.pojo.MCapSpread;
import stocktales.IDS.pojo.PFSchemaRebalUI;
import stocktales.IDS.pojo.PFSchemaRebalUIStats;
import stocktales.IDS.pojo.PFSchemaRebalUIStats_Numbers;
import stocktales.IDS.pojo.ScripMcapCatg;
import stocktales.NFS.enums.EnumMCapClassification;
import stocktales.NFS.model.config.NFSConfig;
import stocktales.basket.allocations.autoAllocation.strategy.pojos.StgyAlloc;
import stocktales.exceptions.SchemaUpdateException;
import stocktales.historicalPrices.enums.EnumInterval;
import stocktales.historicalPrices.srv.intf.ITimeSeriesStgyValuationSrv;
import stocktales.historicalPrices.utility.StockPricesUtility;
import stocktales.money.UtilDecimaltoMoneyString;
import stocktales.strategy.helperPOJO.SectorAllocations;
import yahoofinance.Stock;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IDS_PFSchema_REbalUI_Srv implements stocktales.IDS.srv.intf.IDS_PFSchema_REbalUI_Srv
{

	@Autowired
	private MessageSource msgSrc;

	@Autowired
	private NFSConfig nfsConfig;

	@Autowired
	private ITimeSeriesStgyValuationSrv valSrv;

	@Autowired
	private RepoPFSchema repoPFSchema;

	@Autowired
	private IDS_CorePFSrv pfCorePFSrv;

	private PFSchemaRebalUI rebalContainer;

	@Value("${pf.allocTotalErr}")
	private final String errAllocs = "";

	@Override
	public PFSchemaRebalUI createSchema()
	{

		clearBuffer(); // Clear the Session Memory

		PFSchemaRebalUI newPFSchema = new PFSchemaRebalUI();

		newPFSchema.setMode(EnumMode.Create);
		newPFSchema.setValidated(false);

		this.rebalContainer = newPFSchema;
		return rebalContainer;
	}

	@Override
	public PFSchemaRebalUI uploadSchemaforUpdate() throws Exception
	{
		if (this.rebalContainer == null)
		{
			clearBuffer(); // Clear the Session Memory

			PFSchemaRebalUI newPFSchema = new PFSchemaRebalUI();

			this.rebalContainer = newPFSchema;
		}
		refurbishAllocations(upload4mSchema());

		return this.rebalContainer;

	}

	@Override
	public void addValidateScrips(String scripsStr)
	{
		String[] arrOfNewScrips = scripsStr.split("\\s*,\\s*");
		if (arrOfNewScrips.length > 0)
		{

			// Create List of Scrips
			for (int i = 0; i < arrOfNewScrips.length; i++)
			{
				rebalContainer.getScrips().add(arrOfNewScrips[i]);
			}

			// Remove Duplicate if any
			List<String> uniqueScrips = rebalContainer.getScrips().stream().distinct().collect(Collectors.toList());
			if (uniqueScrips.size() > 0)
			{
				for (String scrip : uniqueScrips)
				{
					// Check if Scrip is already a Part of Schema
					if (rebalContainer.getScAllocMassUpdate().getScAllocList() != null)
					{

						Optional<IDS_SCAlloc> foundO = rebalContainer.getScAllocMassUpdate().getScAllocList().stream()
								.filter(x -> x.getScCode().equals(scrip)).findFirst();
						if (!foundO.isPresent()) // Not Already Existing
						{
							rebalContainer.getScAllocMassUpdate().getScAllocList()
									.add(new IDS_SCAlloc(scrip, "", 0, 0));
						}

					}

				}
				this.rebalContainer.setValidated(false);
			}
		}
	}

	@Override
	public void removeScrip4mSchema(String scCode) throws Exception
	{
		// Remove Scrip from REbalance Container
		if (scCode != null)
		{
			if (scCode.trim().length() > 0)
			{
				Optional<IDS_SCAlloc> exisScripEntryO = rebalContainer.getScAllocMassUpdate().getScAllocList().stream()
						.filter(w -> w.getScCode().equals(scCode)).findFirst();
				if (exisScripEntryO.isPresent())
				{
					rebalContainer.getScAllocMassUpdate().getScAllocList().remove(exisScripEntryO.get());
					// REst the Validated Flag - Needs to be Validated Again
					rebalContainer.setValidated(false);
					// Trigger Allocations REfurbishments
					refurbishAllocations(rebalContainer.getScAllocMassUpdate());
				}
			}
		}

	}

	@Override
	public void clearSelScrips()
	{
		this.rebalContainer.setScripsStr(new String());

	}

	@Override
	public void refurbishAllocations(IDS_ScAllocMassUpdate allocMassUpdate) throws Exception
	{
		if (allocMassUpdate != null)
		{
			if (allocMassUpdate.getScAllocList() != null)
			{
				if (allocMassUpdate.getScAllocList().size() > 0)
				{
					this.rebalContainer.setScAllocMassUpdate(allocMassUpdate);

					/*
					 * Populate The Stats as per Current Scrips on UI
					 */
					populateStats();

					/*
					 * Check the Sum of Incremental and Ideal Allocations If both equals 100 turn on
					 * the validated flag which will turn on the Save Button
					 */
					if ((Precision.round(rebalContainer.getStats().getNumbers().getSumIdealAlloc(), 0) == 100)
							&& (Precision.round(rebalContainer.getStats().getNumbers().getSumIncAlloc(), 0) == 100))
					{
						rebalContainer.setValidated(true);
					} else
					{
						rebalContainer.setValidated(false);
						// FLAG An Exception and return
						throw new SchemaUpdateException(msgSrc.getMessage("pf.allocTotalErr", new Object[]
						{ rebalContainer.getStats().getNumbers().getSumIdealAlloc(),
								rebalContainer.getStats().getNumbers().getSumIncAlloc() }, Locale.ENGLISH));
					}
				}
			}
		}

	}

	@Override
	public PFSchemaRebalUIStats getQuickStatsforSchema() throws Exception
	{
		PFSchemaRebalUIStats stats = null;
		if (rebalContainer.getStats() != null)
		{
			if (rebalContainer.getStats().getNumbers().getNumScrips() > 0)
			{
				stats = this.rebalContainer.getStats();
			}
		} else
		{
			populateStats();
			stats = this.rebalContainer.getStats();
		}

		return stats;
	}

	@Override
	public void commitValidatedSchema(IDS_ScAllocMassUpdate allocMassUpdate) throws Exception
	{
		if (allocMassUpdate != null && pfCorePFSrv != null)
		{
			if (allocMassUpdate.getScAllocList().size() > 0)
			{
				pfCorePFSrv.processAllocationChanges(allocMassUpdate);
			}
		}

	}

	/**
	 * Return SC Alloc Mass Update POJO from Current PF Schema
	 * 
	 */
	private IDS_ScAllocMassUpdate upload4mSchema()
	{
		IDS_ScAllocMassUpdate allocUpdatePOJO = new IDS_ScAllocMassUpdate();

		List<PFSchema> schemaPF = repoPFSchema.findAll();
		for (PFSchema schH : schemaPF)
		{
			IDS_SCAlloc scAlloc = new IDS_SCAlloc(schH.getSccode(), schH.getSector(), schH.getIdealalloc(),
					schH.getIncalloc());
			allocUpdatePOJO.getScAllocList().add(scAlloc);
		}

		return allocUpdatePOJO;

	}

	/**
	 * REcalculate PF Schema Rebalance stats
	 * 
	 * @throws Exception
	 */
	private void populateStats() throws Exception
	{
		if (this.rebalContainer.getScAllocMassUpdate() != null)
		{
			// Flush the Old Stats
			this.rebalContainer.setStats(new PFSchemaRebalUIStats());
			this.rebalContainer.getStats().setNumbers(new PFSchemaRebalUIStats_Numbers());
			this.rebalContainer.getStats().setMCapAllocs(new ArrayList<SectorAllocations>());
			this.rebalContainer.getStats().setScMCapList(new ArrayList<ScripMcapCatg>());
			this.rebalContainer.getStats().setMcapSpread(new ArrayList<MCapSpread>());

			// Calculate the Numbers
			calculateNumbers();

			// Calculate MCap Classifications
			setMcapClassifications();

			// Calculate Returns in Schema
			calculateReturns();

		}
	}

	private void setMcapClassifications() throws Exception
	{
		for (IDS_SCAlloc scAlloc : this.getRebalContainer().getScAllocMassUpdate().getScAllocList())
		{
			Stock quote = StockPricesUtility.getQuoteforScrip(scAlloc.getScCode());
			if (quote != null)
			{
				if (quote.getStats().getMarketCap() != null)
				{
					double Mcap = Precision.round((quote.getStats().getMarketCap().doubleValue() / 10000000), 0);
					EnumMCapClassification mcapName = nfsConfig.getMcapClassificationForMCapKCr(Mcap);

					rebalContainer.getStats().getScMCapList()
							.add(new ScripMcapCatg(scAlloc.getScCode(), Mcap, scAlloc.getIdealAlloc(), mcapName));
				}
			}

		}

		// GRoup for Mcap
		// Grouping and Summing BY
		Map<EnumMCapClassification, Double> allocsPerMCap = rebalContainer.getStats().getScMCapList().stream()
				.collect(Collectors.groupingBy(ScripMcapCatg::getMCapCatgName,
						Collectors.summingDouble(ScripMcapCatg::getAlloc)));

		// Converting Map to List
		if (allocsPerMCap.size() > 0)
		{
			List<SectorAllocations> mCapAllocP = new ArrayList<SectorAllocations>();
			allocsPerMCap.forEach((k, v) -> mCapAllocP.add(new SectorAllocations(k.toString(), v)));
			// Setting Sectors and Allocations Weights
			rebalContainer.getStats().setMCapAllocs(mCapAllocP);
		}

		// Preparing Market Cap Spread Data
		for (SectorAllocations mcapClass : rebalContainer.getStats().getMCapAllocs())
		{
			MCapSpread mcapSpEnt = new MCapSpread();
			mcapSpEnt.setMCapCatgName(mcapClass.getSector());
			mcapSpEnt.setAlloc(mcapClass.getAlloc());

			int numScrips = (int) rebalContainer.getStats().getScMCapList().stream()
					.filter(c -> c.getMCapCatgName().toString().equals(mcapClass.getSector())).count();
			mcapSpEnt.setNumScrips(numScrips);

			rebalContainer.getStats().getMcapSpread().add(mcapSpEnt);
		}

	}

	/*
	 * Calculate the Numbers
	 */
	private void calculateNumbers() throws Exception
	{
		rebalContainer.getStats().getNumbers()
				.setNumScrips(rebalContainer.getScAllocMassUpdate().getScAllocList().size());

		double sumIdealAlloc = rebalContainer.getScAllocMassUpdate().getScAllocList().stream()
				.mapToDouble(IDS_SCAlloc::getIdealAlloc).sum();
		rebalContainer.getStats().getNumbers().setSumIdealAlloc(Precision.round(sumIdealAlloc, 1));

		double sumIncAlloc = rebalContainer.getScAllocMassUpdate().getScAllocList().stream()
				.mapToDouble(IDS_SCAlloc::getIncAlloc).sum();
		rebalContainer.getStats().getNumbers().setSumIncAlloc(Precision.round(sumIncAlloc, 1));

		if (sumIdealAlloc != 100 || sumIncAlloc != 100) // Incremental Allocation Sum != 0
		{
			throw new SchemaUpdateException(msgSrc.getMessage("pf.allocTotalErr", new Object[]
			{ sumIdealAlloc, sumIncAlloc }, Locale.ENGLISH));
		}

		// TOP 5 allocations Sum
		if (rebalContainer.getScAllocMassUpdate().getScAllocList().size() > 4)
		{
			List<IDS_SCAlloc> allocSortedDesc = rebalContainer.getScAllocMassUpdate().getScAllocList().stream()
					.sorted(Comparator.comparingDouble(IDS_SCAlloc::getIdealAlloc).reversed())
					.collect(Collectors.toList());

			List<IDS_SCAlloc> top5Scrips = allocSortedDesc.stream().limit(5).collect(Collectors.toList());

			double sumTop5Allocs = top5Scrips.stream().mapToDouble(IDS_SCAlloc::getIdealAlloc).sum();
			rebalContainer.getStats().getNumbers().setSumtop5Scrips(Precision.round(sumTop5Allocs, 1));

		}

		// Sectors Grouping
		// Grouping and Showing Group Key and Corresponding Entities in each Group - Not
		// needed check
		Map<String, List<IDS_SCAlloc>> allocsPerSectorList = rebalContainer.getScAllocMassUpdate().getScAllocList()
				.stream().collect(Collectors.groupingBy(IDS_SCAlloc::getSector));

		// Grouping and Summing BY
		Map<String, Double> allocsPerSector = rebalContainer.getScAllocMassUpdate().getScAllocList().stream().collect(
				Collectors.groupingBy(IDS_SCAlloc::getSector, Collectors.summingDouble(IDS_SCAlloc::getIdealAlloc)));

		// Converting Map to List
		if (allocsPerSector.size() > 0)
		{
			List<SectorAllocations> secAllocP = new ArrayList<SectorAllocations>();
			allocsPerSector.forEach((k, v) -> secAllocP.add(new SectorAllocations(k, v)));
			// Setting Sectors and Allocations Weights
			rebalContainer.getStats().setSecAllocs(secAllocP);
		}

		rebalContainer.getStats().getNumbers().setNumSectors(rebalContainer.getStats().getSecAllocs().size());

		List<SectorAllocations> scAllocs = new ArrayList<SectorAllocations>();
		// Minimum Amount to Invest as per Schema in Ideal Ratios
		for (IDS_SCAlloc scAlloc : rebalContainer.getScAllocMassUpdate().getScAllocList())
		{
			scAllocs.add(new SectorAllocations(scAlloc.getScCode(), scAlloc.getIdealAlloc()));
		}

		if (scAllocs.size() > 0)
		{
			rebalContainer.getStats().getNumbers()
					.setMinmInv(Precision.round(StockPricesUtility.getMinmAmntforPFCreation(scAllocs), 2));
			rebalContainer.getStats().getNumbers().setMinmInvStr(UtilDecimaltoMoneyString
					.getMoneyStringforDecimal(rebalContainer.getStats().getNumbers().getMinmInv(), 2));
		}

	}

	private void calculateReturns() throws Exception
	{
		if (valSrv != null && this.rebalContainer.getScAllocMassUpdate().getScAllocList().size() > 0)
		{
			List<StgyAlloc> stgyAllocs = new ArrayList<StgyAlloc>();

			for (IDS_SCAlloc scAlloc : this.getRebalContainer().getScAllocMassUpdate().getScAllocList())
			{
				stgyAllocs.add(new StgyAlloc(scAlloc.getScCode(), scAlloc.getIdealAlloc()));
			}

			rebalContainer.getStats()
					.setDateVals(valSrv.getValuationsforScripsAllocList(EnumInterval.Last5Yrs, stgyAllocs));
		}

	}

	private void clearBuffer()
	{
		this.rebalContainer = null;
	}

}
