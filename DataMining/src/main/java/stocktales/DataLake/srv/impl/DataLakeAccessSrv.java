package stocktales.DataLake.srv.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import stocktales.BackTesting.ATH.model.pojo.SC_CMP_52wkPenultimatePrice_Delta;
import stocktales.DataLake.model.entity.DL_ScripPriceATH;
import stocktales.DataLake.model.repo.RepoATHScripPrices;
import stocktales.DataLake.srv.intf.IDataLakeAccessSrv;
import stocktales.durations.UtilDurations;
import stocktales.maths.UtilPercentages;

@Service
@Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_SESSION)
public class DataLakeAccessSrv implements IDataLakeAccessSrv
{

	private List<DL_ScripPriceATH> dataLake = new ArrayList<DL_ScripPriceATH>();

	@Autowired
	private RepoATHScripPrices repoSCPrices;

	@Override
	public List<DL_ScripPriceATH> getPricesAllByDateRange(Calendar from, Calendar to) throws Exception
	{
		List<DL_ScripPriceATH> filteredPricesList = null;
		if (dataLake.size() > 0 && from != null && to != null)
		{
			if (to.after(from))
			{
				// Adjust for Easier Filtering
				to.add(Calendar.DAY_OF_WEEK, -1);
				from.add(Calendar.DAY_OF_WEEK, 1);

				// Filter
				filteredPricesList = dataLake.stream().filter(x ->
				{
					if ((x.getDate().after(from.getTime()) && x.getDate().before(to.getTime())))
					{
						return true;
					}
					return false;
				}

				).collect(Collectors.toList());

			}
		}

		return filteredPricesList;
	}

	@Override
	public List<DL_ScripPriceATH> getPricesForScripByDateRange(String scCode, Calendar from, Calendar to)
			throws Exception
	{
		List<DL_ScripPriceATH> filteredPricesList = null;
		if (dataLake.size() > 0 && from != null && to != null && StringUtils.hasText(scCode))
		{
			if (to.after(from))
			{
				// Adjust for Easier Filtering
				to.add(Calendar.DAY_OF_WEEK, -1);
				from.add(Calendar.DAY_OF_WEEK, 1);

				// Filter
				filteredPricesList = dataLake.stream().filter(x ->
				{
					if (x.getSccode().equals(scCode)
							&& (x.getDate().after(from.getTime()) && x.getDate().before(to.getTime())))
					{
						return true;
					}
					return false;
				}

				).collect(Collectors.toList());

			}
		}

		return filteredPricesList;
	}

	@Override
	public List<DL_ScripPriceATH> getPricesForScripsByDateRange(List<String> scCodes, Calendar from, Calendar to)
			throws Exception
	{
		List<DL_ScripPriceATH> filteredPricesList = null;
		if (dataLake.size() > 0 && from != null && to != null && scCodes != null)
		{
			if (scCodes.size() > 0)
			{
				filteredPricesList = new ArrayList<DL_ScripPriceATH>();
				for (String scrip : scCodes)
				{
					filteredPricesList.addAll(this.getPricesForScripByDateRange(scrip, from, to));
				}
			}

		}

		return filteredPricesList;
	}

	@Override
	public SC_CMP_52wkPenultimatePrice_Delta getLastYrPrice_SMA_DeltaByScrip(String scCode, Calendar startDate)
	{
		SC_CMP_52wkPenultimatePrice_Delta smaP = null;

		if (StringUtils.hasText(scCode) && startDate != null)
		{
			try
			{
				Calendar from = UtilDurations.getTodaysCalendarDateOnly();
				from.setTime(startDate.getTime());
				from.add(Calendar.YEAR, -1);

				List<DL_ScripPriceATH> topN = null;

				List<DL_ScripPriceATH> hqS = this.getPricesForScripByDateRange(scCode, from, startDate);
				if (hqS != null)
				{
					if (hqS.size() >= 100) // Minimum 100 Days data needed - Ignore Otherwise
					{
						// sort by Date Descending
						hqS.sort(Comparator.comparing(DL_ScripPriceATH::getDate).reversed());

						SC_CMP_52wkPenultimatePrice_Delta athData = new SC_CMP_52wkPenultimatePrice_Delta();

						athData.setScCode(scCode);

						athData.setCmp(Precision.round(hqS.get(0).getCloseprice(), 2));
						athData.setLastYrPrice(Precision.round(hqS.get(hqS.size() - 1).getCloseprice(), 2));
						athData.setDelta(
								UtilPercentages.getPercentageDelta(athData.getLastYrPrice(), athData.getCmp(), 1));

						// top 20
						topN = hqS.stream().limit(20).collect(Collectors.toList());
						double sma20 = topN.stream().mapToDouble(DL_ScripPriceATH::getCloseprice).average()
								.getAsDouble();

						athData.setSma20(Precision.round(sma20, 2));
						athData.setSma20Delta(UtilPercentages.getPercentageDelta(sma20, athData.getCmp(), 1));

						// top 50
						topN = hqS.stream().limit(50).collect(Collectors.toList());
						double sma50 = topN.stream().mapToDouble(DL_ScripPriceATH::getCloseprice).average()
								.getAsDouble();
						athData.setSma50(Precision.round(sma50, 2));
						athData.setSma50Delta(UtilPercentages.getPercentageDelta(sma50, athData.getCmp(), 1));

						// top 100
						topN = hqS.stream().limit(100).collect(Collectors.toList());
						double sma100 = topN.stream().mapToDouble(DL_ScripPriceATH::getCloseprice).average()
								.getAsDouble();
						athData.setSma100(Precision.round(sma100, 2));
						athData.setSma100Delta(UtilPercentages.getPercentageDelta(sma100, athData.getCmp(), 1));

					}
				}
			} catch (Exception e)
			{
				// Invalid Scrip Code - Do nothing
			}
		}

		return smaP;

	}

	@PostConstruct
	private void initializeDataLake()
	{
		if (repoSCPrices != null)
		{
			this.dataLake = repoSCPrices.findAll();
		}
	}

}
