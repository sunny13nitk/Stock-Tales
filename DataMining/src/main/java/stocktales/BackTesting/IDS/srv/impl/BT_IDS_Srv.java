package stocktales.BackTesting.IDS.srv.impl;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stocktales.BackTesting.IDS.pojo.BT_AD_IDS;
import stocktales.BackTesting.IDS.pojo.BT_EP_IDS;
import stocktales.BackTesting.IDS.pojo.BT_IP_IDS;
import stocktales.BackTesting.IDS.pojo.BT_PFSchema;
import stocktales.BackTesting.IDS.pojo.BT_Sc_HPricesSize;
import stocktales.BackTesting.IDS.pojo.BT_ScripAllocs;
import stocktales.BackTesting.IDS.srv.intf.IBT_IDS_Srv;
import stocktales.BackTesting.IDS.srv.intf.IBT_IDS_ValdationSrv;
import stocktales.IDS.enums.EnumSMABreach;
import stocktales.IDS.enums.EnumVolatilityProfile;
import stocktales.IDS.model.pf.entity.HC;
import stocktales.IDS.model.pf.entity.HCI;
import stocktales.IDS.pojo.IDS_SMAPreview;
import stocktales.IDS.pojo.IDS_SMASpread;
import stocktales.IDS.pojo.IDS_ScSMASpread;
import stocktales.IDS.pojo.IDS_VPDetails;
import stocktales.IDS.srv.intf.IDS_DeploymentAmntSrv;
import stocktales.IDS.srv.intf.IDS_VPSrv;
import stocktales.NFS.model.pojo.NFSStockHistoricalQuote;
import stocktales.historicalPrices.utility.StockPricesUtility;
import stocktales.usersPF.enums.EnumTxnType;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

@Service
public class BT_IDS_Srv implements IBT_IDS_Srv
{
	@Autowired
	private IBT_IDS_ValdationSrv valdSrv;

	@Autowired
	private IDS_VPSrv vpSrv;

	@Autowired
	private IDS_DeploymentAmntSrv depAmntSrv;

	private BT_EP_IDS btContainer;

	private static final long daysFreshBuyGap = 20;

	@Override
	public BT_EP_IDS backTestIDS(BT_IP_IDS ip_params) throws Exception
	{

		/**
		 * Validate Parameters and Prepare Admin. Data
		 */
		initialize(ip_params);

		/**
		 * Load Prices Data for durations calibration
		 */
		loadPrices();

		/**
		 * REConfigue FRom and Curr Duration as per Scrips History Available
		 */
		reconfigureDurations();

		/**
		 * REload - Prices for subsequent SMA/Purcases calculations - Performance
		 */
		reloadPrices();

		/**
		 * Prepare PF Schema
		 */
		preparePFSchema();

		/**
		 * Prepare Volatility Profiles
		 */

		updateVolProfile();

		/**
		 * rolltheDice - Start the Processing/Simulation Now
		 */
		rolltheDice();

		return this.btContainer;
	}

	private void reloadPrices() throws Exception
	{

		// Clear prices
		btContainer.getCalcData().getPriceData().clear();

		/**
		 * REpopulate as per FRom Date : FRom Date - 1 year To Date : To Date
		 */
		if (btContainer.getAdminData().getIp_params().getScAllocs() != null)
		{
			Calendar penYear4m = Calendar.getInstance(); // Always get New Instance

			penYear4m.setTime(btContainer.getAdminData().getCalFrom().getTime());
			penYear4m.add(Calendar.YEAR, -1);

			for (BT_ScripAllocs scAlloc : btContainer.getAdminData().getIp_params().getScAllocs())
			{
				// 1. Get the Prices Simply for the said Duration, amount frequency
				NFSStockHistoricalQuote scHistoricaPrices = StockPricesUtility.getHistoricalPricesforScripsEndatPast(
						scAlloc.getScCode(), penYear4m, btContainer.getAdminData().getCalTo(), Interval.DAILY);

				if (scHistoricaPrices != null)
				{
					if (scHistoricaPrices.getQuotesH().size() > 0)
					{
						btContainer.getCalcData().getPriceData().add(scHistoricaPrices);
					}
				}

			}

		}

	}

	private void reconfigureDurations()
	{
		if (btContainer.getCalcData().getPriceData() != null)
		{
			if (btContainer.getCalcData().getPriceData().size() > 0)
			{
				for (NFSStockHistoricalQuote scH : btContainer.getCalcData().getPriceData())
				{
					BT_Sc_HPricesSize scHSize = new BT_Sc_HPricesSize(scH.getScCode(), scH.getQuotesH().size());
					btContainer.getCalcData().getScPHist().add(scHSize);
				}
			}
		}

		/**
		 * GEt Min'm History and Max History
		 */
		int minDaysH = btContainer.getCalcData().getScPHist().stream().mapToInt(BT_Sc_HPricesSize::getSize).min()
				.getAsInt();

		int maxDaysH = btContainer.getCalcData().getScPHist().stream().mapToInt(BT_Sc_HPricesSize::getSize).max()
				.getAsInt();

		if (maxDaysH > minDaysH)
		{
			// RE-configuration of durations needed
			// Populate Min'm Duration Scrip
			btContainer.getCalcData().setMinHistoryScrip(btContainer.getCalcData().getScPHist().stream()
					.filter(w -> w.getSize() == minDaysH).findFirst().get());

			// From Will begin from this Scrip's Listing Date
			NFSStockHistoricalQuote hQMin = btContainer.getCalcData().getPriceData().stream()
					.filter(y -> y.getScCode().equals(btContainer.getCalcData().getMinHistoryScrip().getScCode()))
					.findFirst().get();
			if (hQMin != null)
			{
				if (hQMin.getQuotesH().size() > 0)
				{
					btContainer.getAdminData().setCalFrom(hQMin.getQuotesH().get(0).getDate()); // Start Calendar
					btContainer.getAdminData().setStartDate(hQMin.getQuotesH().get(0).getDate().getTime()); // Start
																											// Date

					btContainer.getAdminData().setCalCurr(hQMin.getQuotesH().get(0).getDate());// Current Iteration
																								// starting Today
				}

				/**
				 * Set the Market Open Days as per Min'n History Scrip
				 */
				hQMin.getQuotesH().stream().filter(x -> btContainer.getCalcData().getMarketDays().add(x.getDate()))
						.collect(Collectors.toList());
			}

		}

		btContainer.getAdminData().setNumDaysIterations(minDaysH); // Update Total Loop Passes Also

	}

	private void rolltheDice() throws Exception
	{
		/**
		 * For Each Market day
		 */
		int daysPass = 1;

		if (btContainer.getCalcData().getMarketDays().size() > 0)
		{
			for (Calendar currDate : btContainer.getCalcData().getMarketDays())
			{
				btContainer.getAdminData().setCalCurr(currDate);
				if (daysPass == 1)
				{
					/*
					 * For the 1st Day - Buy for Lump Sum and IDS
					 */

					buyStocks();
					buyStocksIDS();

				}

				// Increment the Current Date as per Trade Days & Loop Pass
				daysPass++;

			}
		}

	}

	/**
	 * Buy Stocks for IDS PF
	 * 
	 * @throws Exception
	 */
	private void buyStocksIDS() throws Exception
	{
		/*
		 * Get Proposals if Any for current Date
		 */
		List<HCI> buyPS = this.getBuyProposals4CurrDate();
		if (buyPS != null)
		{
			if (buyPS.size() > 0)
			{
				for (HCI hci : buyPS)
				{
					BT_PFSchema pfSchema = this.btContainer.getCalcData().getPfSchema().stream()
							.filter(f -> f.getSccode().equals(hci.getSccode())).findFirst().get();
					processPurchaseforIDS(pfSchema, hci);
				}

			}
		}
	}

	/**
	 * Buy Stocks for normal PF
	 * 
	 */
	private void buyStocks()
	{

		/**
		 * Looping over Schema get Current Day Price for Scrip and Purchase
		 */
		for (BT_PFSchema pfSchema : btContainer.getCalcData().getPfSchema())
		{

			if (pfSchema.getDepamnt() > 0)
			{
				// 1. Get Closing Price for Current date
				Optional<NFSStockHistoricalQuote> hqO = btContainer.getCalcData().getPriceData().stream()
						.filter(e -> e.getScCode().equals(pfSchema.getSccode())).findFirst();
				if (hqO.isPresent())
				{
					if (hqO.get().getQuotesH().size() > 0)
					{
						Optional<HistoricalQuote> HPriceO = hqO.get().getQuotesH().stream()
								.filter(t -> t.getDate().compareTo(btContainer.getAdminData().getCalCurr()) == 0)
								.findFirst();
						if (HPriceO.isPresent())
						{
							double cmp = HPriceO.get().getClose().doubleValue();

							// 2. Get Number of Units as per DepAmnt
							int units = (int) (pfSchema.getDepamnt() / cmp);

							if ((units * cmp) > pfSchema.getDepamnt())
							{
								units--;
							}

							if (units > 0)
							{
								// Process Normal PF Purchase - false for IDS
								processPurchase(pfSchema, units, cmp);
							}

						}
					}
				}

			}

		}

	}

	private void processPurchaseforIDS(BT_PFSchema pfSchema, HCI hci)
	{

		boolean newPurchase = false;

		pfSchema.setDepamnt(pfSchema.getDepamnt() - (hci.getUnits() * hci.getTxnppu())); // Set Dep Amnt
		// Populate HC
		if (btContainer.getCalcData().getHC_IDS().size() > 0)
		{
			// Scan for Current Scrip if Previously Bought
			Optional<HC> HCO = btContainer.getCalcData().getHC_IDS().stream()
					.filter(e -> e.getSccode().equals(pfSchema.getSccode())).findFirst();
			if (HCO.isPresent())
			{
				HC hc = HCO.get();
				// Adjust PPU and Units
				double totInv = hc.getUnits() * hc.getPpu() + (hci.getUnits() * hci.getTxnppu());
				int totalUnits = hc.getUnits() + hci.getUnits();

				hc.setPpu(Precision.round(totInv / totalUnits, 2));
				hc.setUnits(totalUnits);

			} else
			{
				newPurchase = true;
			}

		} else
		{
			newPurchase = true;
		}

		if (newPurchase)
		{
			btContainer.getCalcData().getHC_IDS()
					.add(new HC(pfSchema.getSccode(), hci.getTxnppu(), hci.getUnits(), hci.getSmarank()));
		}

		// Populate HCI
		btContainer.getCalcData().getHCI_IDS().add(hci);

	}

	private void processPurchase(BT_PFSchema pfSchema, int units, double cmp)
	{
		boolean newPurchase = false;

		pfSchema.setDepamnt(pfSchema.getDepamnt() - (units * cmp)); // Set Dep Amnt
		// Populate HC
		if (btContainer.getCalcData().getHC().size() > 0)
		{
			// Scan for Current Scrip if Previously Bought
			Optional<HC> HCO = btContainer.getCalcData().getHC().stream()
					.filter(e -> e.getSccode().equals(pfSchema.getSccode())).findFirst();
			if (HCO.isPresent())
			{
				HC hc = HCO.get();
				// Adjust PPU and Units
				double totInv = hc.getUnits() * hc.getPpu() + (units * cmp);
				int totalUnits = hc.getUnits() + units;

				hc.setPpu(Precision.round(totInv / totalUnits, 2));
				hc.setUnits(totalUnits);

			} else
			{
				newPurchase = true;
			}

		} else
		{
			newPurchase = true;
		}

		if (newPurchase)
		{
			btContainer.getCalcData().getHC().add(new HC(pfSchema.getSccode(), cmp, units, 0));
		}

		// Populate HCI
		btContainer.getCalcData().getHCI()
				.add(new HCI(0, pfSchema.getSccode(), btContainer.getAdminData().getCalCurr().getTime(),
						stocktales.usersPF.enums.EnumTxnType.Buy, units, cmp, 0));

	}

	private void loadPrices() throws Exception
	{
		if (btContainer.getAdminData().getIp_params().getScAllocs() != null)
		{

			for (BT_ScripAllocs scAlloc : btContainer.getAdminData().getIp_params().getScAllocs())
			{
				// 1. Get the Prices Simply for the said Duration, amount frequency
				NFSStockHistoricalQuote scHistoricaPrices = StockPricesUtility.getHistoricalPricesforScrips(
						scAlloc.getScCode(), Calendar.YEAR,
						btContainer.getAdminData().getIp_params().getStartSinceLastYrs(), Interval.DAILY);

				if (scHistoricaPrices != null)
				{
					if (scHistoricaPrices.getQuotesH().size() > 0)
					{
						btContainer.getCalcData().getPriceData().add(scHistoricaPrices);
					}
				}

			}

		}

	}

	/**
	 * Update Volatility Profile as per Current Date
	 * 
	 * @throws Exception
	 */
	private void updateVolProfile() throws Exception
	{
		if (vpSrv != null)
		{
			Calendar from = Calendar.getInstance();
			from.setTime(btContainer.getAdminData().getCalCurr().getTime());
			from.add(Calendar.YEAR, -1);

			Calendar to = btContainer.getAdminData().getCalCurr();

			btContainer.getCalcData().getVolProfiles().clear(); // Clear existing Profiles if Any

			for (BT_PFSchema pfschema : btContainer.getCalcData().getPfSchema())
			{
				NFSStockHistoricalQuote scPriceHistory = new NFSStockHistoricalQuote(pfschema.getSccode(),
						getLast1YearPrices4mBufferforCurrDate(pfschema.getSccode()));

				IDS_VPDetails vprofile = vpSrv.getVolatilityProfileDetails4Scrip4PriceHistory(pfschema.getSccode(),
						scPriceHistory);

				btContainer.getCalcData().getVolProfiles().add(vprofile);
			}
		}

	}

	private void preparePFSchema()
	{

		double alloc = 0;
		BT_PFSchema pfschema;

		/**
		 * Distribute Allocs Evenly in case of equi-weight
		 */
		if (btContainer.getAdminData().getIp_params().isEquiwt())
		{
			alloc = Precision.round(100 / btContainer.getAdminData().getIp_params().getScAllocs().size(), 1);
		}

		// Prepare Schema Allocation for Each Scrip
		for (BT_ScripAllocs scAlloc : btContainer.getAdminData().getIp_params().getScAllocs())
		{
			if (alloc > 0)
			{
				pfschema = new BT_PFSchema(scAlloc.getScCode(), alloc, 0);
			} else
			{
				pfschema = new BT_PFSchema(scAlloc.getScCode(), scAlloc.getAlloc(), 0);
			}

			this.btContainer.getCalcData().getPfSchema().add(pfschema);

		}

		/**
		 * Distribute as per Allocs Assigned
		 */
		addMoneytoSchema(btContainer.getAdminData().getIp_params().getLumpSumInv());
	}

	private void addMoneytoSchema(double amounttoAdd)
	{
		for (BT_PFSchema pfschema : btContainer.getCalcData().getPfSchema())
		{
			double amount = Precision.round(amounttoAdd * pfschema.getIncalloc() * .01, 2);
			pfschema.setDepamnt(amount);
		}

	}

	private void initialize(BT_IP_IDS ip_params) throws Exception
	{

		if (valdSrv != null)
		{
			BT_AD_IDS adminData = valdSrv.validateParams(ip_params);
			if (adminData != null)
			{
				btContainer = new BT_EP_IDS();
				btContainer.setAdminData(valdSrv.validateParams(ip_params));
			}

		}

	}

	private List<HistoricalQuote> getLast1YearPrices4mBufferforCurrDate(String scCode)
	{
		List<HistoricalQuote> pricesTab = null;

		Calendar toDate = Calendar.getInstance();
		Calendar fromDate = Calendar.getInstance();
		toDate.setTime(btContainer.getAdminData().getCalCurr().getTime());
		fromDate.setTime(btContainer.getAdminData().getCalCurr().getTime());
		fromDate.add(Calendar.YEAR, -1);

		int idx4m = 0, idxTo = 0;
		boolean returnNull = false;

		if (scCode != null && btContainer.getCalcData().getPriceData().size() > 0)
		{
			NFSStockHistoricalQuote hQ = btContainer.getCalcData().getPriceData().stream()
					.filter(v -> v.getScCode().equals(scCode)).findFirst().get();
			if (hQ != null)
			{
				// Seek to Index
				Optional<HistoricalQuote> hPE = hQ.getQuotesH().stream().filter(r -> r.getDate().compareTo(toDate) == 0)
						.findFirst();

				if (hPE.isPresent())
				{
					idxTo = hQ.getQuotesH().indexOf(hPE.get());
				} else
				{
					// Do Nothing return Null - No price history found for that duration
					// (e.g. 15/2/2019 to 15/2/2020)
					returnNull = true;
				}

				if (returnNull == false)
				{
					// Seek from Index
					Optional<HistoricalQuote> hPO = hQ.getQuotesH().stream()
							.filter(r -> r.getDate().compareTo(fromDate) == 0).findFirst();

					if (hPO.isPresent())
					{
						idx4m = hQ.getQuotesH().indexOf(hPO.get());
					} else
					{
						idx4m = 0; // Ist Element
					}

					pricesTab = hQ.getQuotesH().subList(idx4m, idxTo);

				}

			}

		}

		return pricesTab;
	}

	public List<HCI> getBuyProposals4CurrDate() throws Exception
	{

		List<HCI> buyProps = null;

		List<BT_PFSchema> pfSchEntities = btContainer.getCalcData().getPfSchema();
		if (pfSchEntities.size() > 0)
		{
			// Only where deployable amnt > 0
			List<BT_PFSchema> pfSchEntitiesPosDepAmnts = pfSchEntities.stream().filter(x -> x.getDepamnt() > 0)
					.collect(Collectors.toList());
			if (pfSchEntitiesPosDepAmnts.size() > 0)
			{

				List<IDS_SMAPreview> pfSMAPvw = this.getPFSchemaSMAPreview();
				if (pfSMAPvw != null)
				{
					if (pfSMAPvw.size() > 0)
					{
						buyProps = new ArrayList<HCI>();
						Date lastBuyDate = null;

						Date dateToday = btContainer.getAdminData().getCalCurr().getTime();

						for (BT_PFSchema pfSchema : pfSchEntitiesPosDepAmnts)
						{
							boolean isPurchase = false;
							boolean isBreached = false;
							double buyAmnt = 0;
							double cmpScrip = 0;
							// Get Volatility Profile for Scrip
							EnumVolatilityProfile scVP = btContainer.getCalcData().getVolProfiles().stream()
									.filter(g -> g.getSccode().equals(pfSchema.getSccode())).findFirst().get()
									.getVolprofile();

							Optional<IDS_SMAPreview> smaPwO = pfSMAPvw.stream()
									.filter(w -> w.getScCode().equals(pfSchema.getSccode())).findFirst();
							if (smaPwO.isPresent() && scVP != null)
							{
								IDS_SMAPreview smaPWUS = smaPwO.get();
								IDS_SMAPreview smaPW = this.getSMASortedforIDS(smaPWUS);
								List<HCI> hcIListBuyScrip = new ArrayList<HCI>();

								/**
								 * GEt All Purchase Txns. for Scrip
								 */
								if (this.btContainer.getCalcData().getHCI_IDS().size() > 0)
								{
									hcIListBuyScrip = btContainer.getCalcData().getHCI_IDS().stream().filter(x ->
									{

										/*
										 * Filtering when the Scrip Code in Price history matches and also the Date with
										 * Current Date - from Date
										 */

										if (x.getSccode().equals(pfSchema.getSccode())
												&& (x.getTxntype() == stocktales.usersPF.enums.EnumTxnType.Buy))
										{
											return true;
										}
										return false;

									}).collect(Collectors.toList());

								}

								// Store Last Buy Date
								if (hcIListBuyScrip.size() > 0)
								{
									lastBuyDate = hcIListBuyScrip.get(hcIListBuyScrip.size() - 1).getDate();
								}

								// Get Close Price of Scrip on Current Date

								NFSStockHistoricalQuote HQ = btContainer.getCalcData().getPriceData().stream()
										.filter(t -> t.getScCode().equals(pfSchema.getSccode())).findFirst().get();
								if (HQ != null)
								{
									cmpScrip = HQ.getQuotesH().stream().filter(
											r -> r.getDate().compareTo(btContainer.getAdminData().getCalCurr()) == 0)
											.findFirst().get().getClose().doubleValue();
								}

								/*
								 * Check - CMP below any SMA
								 */

								// SMA4
								if (cmpScrip < smaPW.getSMAI4())
								{
									/*
									 * Last breach was sma4 further buy can happen when - the last txn was since 20
									 * days at least OR - the last txn was for sma1, sma2, sma3
									 */
									if (lastBuyDate != null)
									{
										if (TimeUnit.DAYS.convert(
												(btContainer.getAdminData().getCalCurr().getTimeInMillis()
														- lastBuyDate.getTime()),
												TimeUnit.MILLISECONDS) > daysFreshBuyGap)
										{
											isPurchase = true;
										}
									} else // No Purchase History for Scrip
									{
										isPurchase = true;
									}

									if (hcIListBuyScrip.size() > 0)
									{
										if (lastBuyDate != null)
										{
											HCI lastBuyEnt = null;
											// Get last buy smarank
											// Date Error
											Date LBD = lastBuyDate;

											List<HCI> hciLastBuyAllforDate = hcIListBuyScrip.stream()
													.filter(u -> u.getDate().compareTo(LBD) == 0)
													.collect(Collectors.toList());
											if (hciLastBuyAllforDate != null)
											{
												if (hciLastBuyAllforDate.size() > 1)
												{
													List<HCI> hciLastBuyAllforDateSorted = hciLastBuyAllforDate.stream()
															.sorted(Comparator.comparingLong(HCI::getTid).reversed())
															.collect(Collectors.toList());
													lastBuyEnt = hciLastBuyAllforDateSorted.get(0);
												} else
												{
													lastBuyEnt = hciLastBuyAllforDate.get(0);
												}
											}

											if (lastBuyEnt != null)
											{
												if (lastBuyEnt.getSmarank() < EnumSMABreach.sma4.ordinal())
												{
													isPurchase = true;
												}
											}
										}
									}

									if (isPurchase)
									{

										// Determine Buy Amount
										buyAmnt = depAmntSrv.getDeploymentAmountByScrip4mPF(pfSchema.getSccode(),
												EnumSMABreach.sma4, pfSchema, scVP);
										if (buyAmnt > 0)
										{
											HCI newPurchase = new HCI();
											newPurchase.setDate(btContainer.getAdminData().getCalCurr().getTime());
											newPurchase.setSccode(pfSchema.getSccode());
											newPurchase.setSmarank(EnumSMABreach.sma4.ordinal());
											newPurchase.setTxntype(EnumTxnType.Buy);
											newPurchase.setUnits((int) Precision.round((buyAmnt / cmpScrip), 0,
													RoundingMode.DOWN.ordinal()));
											newPurchase.setTxnppu(Precision.round(cmpScrip, 2));
											isBreached = true;
										}

									}

								}

								// SMA3
								if (cmpScrip < smaPW.getSMAI3() && !isBreached)
								{
									/*
									 * Last breach was sma3 further buy can happen when - the last txn was since 20
									 * days at least OR - the last txn was for sma1 or sma2
									 */
									if (lastBuyDate != null)
									{
										if (TimeUnit.DAYS.convert((dateToday.getTime() - lastBuyDate.getTime()),
												TimeUnit.MILLISECONDS) > daysFreshBuyGap)
										{
											isPurchase = true;
										}
									} else // No Purchase History for Scrip
									{
										isPurchase = true;
									}

									if (hcIListBuyScrip.size() > 0)
									{
										if (lastBuyDate != null)
										{
											HCI lastBuyEnt = null;
											// Get last buy smarank
											// Date Error
											Date LBD = lastBuyDate;
											List<HCI> hciLastBuyAllforDate = hcIListBuyScrip.stream()
													.filter(u -> u.getDate().compareTo(LBD) == 0)
													.collect(Collectors.toList());
											if (hciLastBuyAllforDate != null)
											{
												if (hciLastBuyAllforDate.size() > 1)
												{
													List<HCI> hciLastBuyAllforDateSorted = hciLastBuyAllforDate.stream()
															.sorted(Comparator.comparingLong(HCI::getTid).reversed())
															.collect(Collectors.toList());
													lastBuyEnt = hciLastBuyAllforDateSorted.get(0);
												} else
												{
													lastBuyEnt = hciLastBuyAllforDate.get(0);
												}
											}

											if (lastBuyEnt != null)
											{
												if (lastBuyEnt.getSmarank() < EnumSMABreach.sma3.ordinal())
												{
													isPurchase = true;
												}
											}
										}
									}

									if (isPurchase)
									{
										// Determine Buy Amount
										buyAmnt = depAmntSrv.getDeploymentAmountByScrip4mPF(pfSchema.getSccode(),
												EnumSMABreach.sma3, pfSchema, scVP);
										if (buyAmnt > 0)
										{
											HCI newPurchase = new HCI();
											newPurchase.setDate(btContainer.getAdminData().getCalCurr().getTime());
											newPurchase.setSccode(pfSchema.getSccode());
											newPurchase.setSmarank(EnumSMABreach.sma3.ordinal());
											newPurchase.setTxntype(EnumTxnType.Buy);
											newPurchase.setUnits((int) Precision.round((buyAmnt / cmpScrip), 0,
													RoundingMode.DOWN.ordinal()));
											newPurchase.setTxnppu(Precision.round(cmpScrip, 2));
											isBreached = true;
										}
									}

								}

								// SMA2
								if (cmpScrip < smaPW.getSMAI2() && !isBreached)
								{
									/*
									 * Last breach was sma2 further buy can happen when - the last txn was since 20
									 * days at least OR - the last txn was for sma1
									 */

									if (lastBuyDate != null)
									{
										if (TimeUnit.DAYS.convert((dateToday.getTime() - lastBuyDate.getTime()),
												TimeUnit.MILLISECONDS) > daysFreshBuyGap)
										{
											isPurchase = true;
										}
									} else // No Purchase History for Scrip
									{
										isPurchase = true;
									}

									if (hcIListBuyScrip.size() > 0)
									{
										if (lastBuyDate != null)
										{
											HCI lastBuyEnt = null;
											// Get last buy smarank
											// Date Error
											Date LBD = lastBuyDate;

											List<HCI> hciLastBuyAllforDate = hcIListBuyScrip.stream()
													.filter(u -> u.getDate().compareTo(LBD) == 0)
													.collect(Collectors.toList());
											if (hciLastBuyAllforDate != null)
											{
												if (hciLastBuyAllforDate.size() > 1)
												{
													List<HCI> hciLastBuyAllforDateSorted = hciLastBuyAllforDate.stream()
															.sorted(Comparator.comparingLong(HCI::getTid).reversed())
															.collect(Collectors.toList());
													lastBuyEnt = hciLastBuyAllforDateSorted.get(0);
												} else
												{
													lastBuyEnt = hciLastBuyAllforDate.get(0);
												}
											}

											if (lastBuyEnt != null)
											{

												/*
												 * Last breach for less than 20 days should be shallower than current
												 * SMA Breach - then only current Breach can be invoked
												 */
												if (lastBuyEnt.getSmarank() < EnumSMABreach.sma2.ordinal())
												{
													isPurchase = true;
												}
											}
										}
									}

									if (isPurchase)
									{
										// Determine Buy Amount
										buyAmnt = depAmntSrv.getDeploymentAmountByScrip4mPF(pfSchema.getSccode(),
												EnumSMABreach.sma2, pfSchema, scVP);
										if (buyAmnt > 0)
										{
											HCI newPurchase = new HCI();
											newPurchase.setDate(btContainer.getAdminData().getCalCurr().getTime());
											newPurchase.setSccode(pfSchema.getSccode());
											newPurchase.setSmarank(EnumSMABreach.sma2.ordinal());
											newPurchase.setTxntype(EnumTxnType.Buy);
											newPurchase.setUnits((int) Precision.round((buyAmnt / cmpScrip), 0,
													RoundingMode.DOWN.ordinal()));
											newPurchase.setTxnppu(Precision.round(cmpScrip, 2));
											isBreached = true;
										}
									}

								}

								// SMA1 - Only 20 days gap or fresh BUY
								if (cmpScrip < smaPW.getSMAI1() && !isBreached)
								{
									// Last breach was sma1 so no further buy since 20 days at least
									if (lastBuyDate != null)
									{
										if (TimeUnit.DAYS.convert((dateToday.getTime() - lastBuyDate.getTime()),
												TimeUnit.MILLISECONDS) > daysFreshBuyGap)
										{
											isPurchase = true;
										}
									} else // No Purchase History for Scrip
									{
										isPurchase = true;
									}

									if (isPurchase)
									{
										// Determine Buy Amount
										buyAmnt = depAmntSrv.getDeploymentAmountByScrip4mPF(pfSchema.getSccode(),
												EnumSMABreach.sma4, pfSchema, scVP);
										if (buyAmnt > 0)
										{
											HCI newPurchase = new HCI();
											newPurchase.setDate(btContainer.getAdminData().getCalCurr().getTime());
											newPurchase.setSccode(pfSchema.getSccode());
											newPurchase.setSmarank(EnumSMABreach.sma4.ordinal());
											newPurchase.setTxntype(EnumTxnType.Buy);
											newPurchase.setUnits((int) Precision.round((buyAmnt / cmpScrip), 0,
													RoundingMode.DOWN.ordinal()));
											newPurchase.setTxnppu(Precision.round(cmpScrip, 2));
											isBreached = true;
										}
									}

								}

							}

						}

					}
				}
			}
		}

		return buyProps;

	}

	private List<IDS_SMAPreview> getPFSchemaSMAPreview() throws Exception
	{
		List<IDS_SMAPreview> pfSMAPreview = null;

		if (btContainer.getCalcData().getPfSchema().size() > 0)
		{
			pfSMAPreview = new ArrayList<IDS_SMAPreview>();
			int[] smaIntervals = new int[]
			{ 18, 40, 70, 170 };

			for (BT_PFSchema pfSchema : btContainer.getCalcData().getPfSchema())
			{
				// 1. Get the SMA Details for the Scrip
				Calendar from = Calendar.getInstance();
				Calendar to = Calendar.getInstance();

				from.setTime(btContainer.getAdminData().getCalCurr().getTime());
				from.add(Calendar.YEAR, -1); // Penultimate Date from Current - FROM
				to.setTime(btContainer.getAdminData().getCalCurr().getTime()); // Current Date Pass - TO

				NFSStockHistoricalQuote scHistoricalPrices = StockPricesUtility
						.getHistoricalPricesforScripsEndatPast(pfSchema.getSccode(), from, to, Interval.DAILY);

				IDS_ScSMASpread scSMASpread = null;
				try
				{
					scSMASpread = StockPricesUtility.getSMASpread4Scrip4HistoricaPriceData(pfSchema.getSccode(),
							smaIntervals, scHistoricalPrices);

				} catch (Exception e)
				{
					// DO nothing Ignore that Scrip
				}
				if (scSMASpread != null)
				{
					if (scSMASpread.getPrSMAList().size() > 0)
					{
						IDS_SMASpread smaSpread = scSMASpread.getPrSMAList().get(scSMASpread.getPrSMAList().size() - 1);
						if (smaSpread != null)
						{
							IDS_SMAPreview smaPreview = new IDS_SMAPreview();
							smaPreview.setScCode(pfSchema.getSccode());
							smaPreview.setClosePrice(Precision.round(smaSpread.getClosePrice(), 0));
							smaPreview.setSMAI1(Precision.round(smaSpread.getSMAI1(), 0));
							smaPreview.setSMAI2(Precision.round(smaSpread.getSMAI2(), 0));
							smaPreview.setSMAI3(Precision.round(smaSpread.getSMAI3(), 0));
							smaPreview.setSMAI4(Precision.round(smaSpread.getSMAI4(), 0));
							pfSMAPreview.add(smaPreview);
						}
					}

				}
			}
		}

		return pfSMAPreview;
	}

	private IDS_SMAPreview getSMASortedforIDS(IDS_SMAPreview smaPvwO)
	{
		IDS_SMAPreview smaIDS = smaPvwO;

		double[] smaS = new double[4];
		int i = 0;

		if (smaPvwO.getSMAI1() > 0)
		{
			smaS[i] = smaPvwO.getSMAI1();
			i++;
		}

		if (smaPvwO.getSMAI2() > 0)
		{
			smaS[i] = smaPvwO.getSMAI2();
			i++;
		}

		if (smaPvwO.getSMAI3() > 0)
		{
			smaS[i] = smaPvwO.getSMAI3();
			i++;
		}

		if (smaPvwO.getSMAI4() > 0)
		{
			smaS[i] = smaPvwO.getSMAI4();
			i++;
		}

		Arrays.sort(smaS);

		smaIDS.setSMAI4(smaS[0]);
		smaIDS.setSMAI3(smaS[1]);
		smaIDS.setSMAI2(smaS[2]);
		smaIDS.setSMAI1(smaS[3]);

		return smaIDS;
	}

	private void adjustSIPInflows() throws Exception
	{

	}

	private void prepareReports() throws Exception
	{

	}

}
