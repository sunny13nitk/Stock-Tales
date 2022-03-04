package stocktales.BackTesting.IDS.srv.impl;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stocktales.BackTesting.IDS.exceptions.IDSBackTestingException;
import stocktales.BackTesting.IDS.pojo.BT_AD_IDS;
import stocktales.BackTesting.IDS.pojo.BT_IP_IDS;
import stocktales.BackTesting.IDS.pojo.BT_ScripAllocs;
import stocktales.BackTesting.IDS.srv.intf.IBT_IDS_ValdationSrv;
import stocktales.NFS.repo.RepoBseData;
import stocktales.durations.UtilDurations;

@Service
public class BT_IDS_ValidationSrv implements IBT_IDS_ValdationSrv
{
	@Autowired
	private RepoBseData repoBSEData;

	private List<String> scCodesList;

	@Override
	public BT_AD_IDS validateParams(BT_IP_IDS ip_params) throws IDSBackTestingException
	{
		BT_AD_IDS adminData = null;
		if (ip_params != null && repoBSEData != null)
		{
			if (scCodesList == null)
			{
				this.scCodesList = repoBSEData.findAllNseCodes();
			}
			// Perform basic Sanity Checks - Assign IP Params if Pass
			doSanityChecks(ip_params);

			// Populate Admin Data
			adminData = populateAdminData(ip_params);
		}

		return adminData;

	}

	private void doSanityChecks(BT_IP_IDS ip_params) throws IDSBackTestingException
	{
		if (ip_params != null)
		{
			if (ip_params.getStartSinceLastYrs() <= 0) // Positive Past Interval
			{
				throw new IDSBackTestingException("Interval to Backtest should be positive!");
			}

			// Lump Sum or SIP should be provided
			if (ip_params.getLumpSumInv() > 0 || ip_params.getSipInfows() > 0)
			{
				// SIP frequency should be positive if SIP to be Done
				if (ip_params.getSipInfows() > 0)
				{
					if (!(ip_params.getSipFrequency() > 0))
					{
						throw new IDSBackTestingException("SIP frequency should be positive for SIPs to be Executed!");
					}
				}
				if (ip_params.getLumpSumInv() <= 0)
				{
					throw new IDSBackTestingException("Lump Sum Investment must be provided");
				}
			} else
			{
				throw new IDSBackTestingException("Either a Lump Sum or an SIP Investment should be provided");
			}

			if (ip_params.getVpUpdateFrequency() <= 0)
			{
				throw new IDSBackTestingException("Volatility Profile(s) update should be positive!");
			}

			if (ip_params.getScAllocs() != null)
			{
				if (ip_params.getScAllocs().size() > 0)
				{
					validateAllocations(ip_params); // Validate Allocations

					validateScripCodes(ip_params); // Validate Scrip Codes

				} else
				{
					throw new IDSBackTestingException("No Scrips maintained for Back Testing!");
				}
			} else
			{
				throw new IDSBackTestingException("No Scrips maintained for Back Testing!");
			}
		}
	}

	private void validateScripCodes(BT_IP_IDS ip_params) throws IDSBackTestingException
	{
		for (BT_ScripAllocs alloc : ip_params.getScAllocs())
		{
			Optional<String> scFoundO = this.scCodesList.stream().filter(s -> s.equals(alloc.getScCode())).findFirst();
			if (!scFoundO.isPresent())
			{
				throw new IDSBackTestingException("Scrip Code -  " + alloc.getScCode() + " is Invalid NSE Code !");
			}
		}

	}

	private void validateAllocations(BT_IP_IDS ip_params)
	{
		/**
		 * Any Allocation Zero; then it has to be equi-allocated else Error!
		 */
		Optional<BT_ScripAllocs> allocZeroO = ip_params.getScAllocs().stream().filter(x -> x.getAlloc() == 0)
				.findFirst();
		if (allocZeroO.isPresent())
		{
			if (!ip_params.isEquiwt())
			{
				throw new IDSBackTestingException("Scrip Allocation(s) are zero for at least Scrip : "
						+ allocZeroO.get().getScCode() + " and Equiweight not set! ");
			}
		}

		/**
		 * Sum of Allocations to be 100 else Error! - If not Equi-weighted
		 */
		if (!ip_params.isEquiwt())
		{
			double sumAllocs = Precision
					.round(ip_params.getScAllocs().stream().mapToDouble(BT_ScripAllocs::getAlloc).sum(), 1);
			if (sumAllocs != 100)
			{
				throw new IDSBackTestingException("Sum of Allocations should be 100 - It is : " + sumAllocs);
			}
		}

	}

	private BT_AD_IDS populateAdminData(BT_IP_IDS ip_params) throws IDSBackTestingException
	{
		Calendar calTo = UtilDurations.getTodaysCalendarDateOnly(); // Today Calendar Date

		Calendar cal4m = UtilDurations.getTodaysCalendarDateOnly(); // Today Calendar Date
		int yrsPen = ip_params.getStartSinceLastYrs() * -1;
		cal4m.add(Calendar.YEAR, yrsPen); // From since penultimate years

		BT_AD_IDS adminData = new BT_AD_IDS();
		adminData.setCalFrom(cal4m); // Start Calendar
		adminData.setStartDate(cal4m.getTime()); // Start Date
		adminData.setCalTo(calTo); // End Calendar
		adminData.setCalCurr(cal4m);// Current Iteration starting Since Penultimate

		// Number of Days to Iterate
		adminData.setNumDaysIterations(TimeUnit.MILLISECONDS
				.toDays(UtilDurations.getTodaysDateOnly().getTime() - adminData.getStartDate().getTime()));

		// Validate - SIP Frequency and VP Profile frequency to be less than total
		// iterations
		if (ip_params.getSipInfows() > 0)
		{
			if (ip_params.getSipFrequency() > adminData.getNumDaysIterations())
			{
				throw new IDSBackTestingException("SIP Frequency :  " + ip_params.getSipFrequency()
						+ "is more than Total Number of Days to Traverse for Backtesting :  "
						+ adminData.getNumDaysIterations());
			}
		}
		if (ip_params.getVpUpdateFrequency() > adminData.getNumDaysIterations())
		{
			throw new IDSBackTestingException("VP Update Frequency :  " + ip_params.getVpUpdateFrequency()
					+ "is more than Total Number of Days to Traverse for Backtesting :  "
					+ adminData.getNumDaysIterations());
		}

		adminData.setIp_params(ip_params);

		return adminData;

	}

}
