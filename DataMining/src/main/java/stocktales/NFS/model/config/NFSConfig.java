package stocktales.NFS.model.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.NFS.enums.EnumMCapClassification;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSConfig
{
	// Minimum Months for Which Scrip must Actively Trade
	private int monthsMinTrade;

	// Min'm Mcap in Cr
	private double minMCap;

	// Top 'N' % from DataSet to Consider for Returns Threshold Calculation
	private double topNDataSetPercRR;

	// Returns Ratio Threshold Breach from top N for Identifying Emerging Trends
	private double rrThreholdEmerging;

	// SMA 20 Inclusion Percentage Delta w.r.t CMP
	private double sma20DeltaIncl;

	// SMA 100 Inclusion Percentage Delta w.r.t CMP
	private double sma100DeltaIncl;

	// Consolidated Score - 50 SMA CMP/Delta WT. - Momentum
	private double sma50DeltaIncl;

	// Consolidated Score - 200 SMA CMP/Delta WT. - Momentum
	private double sma200DeltaIncl;

	// Consolidated Score - 20 SMA CMP/Delta WT. - Momentum
	private double wt20SMADelta;

	// Consolidated Score - 100 SMA CMP/Delta WT. - Momentum
	private double wt100SMADelta;

	// Consolidated Score - Average Monthly Return Wt. - Consistency
	private double wtAvgMR;

	// T2T Series Scrips Maximum Percentage in PF
	private double t2tMaxPer;

	// Ideal Portfolio Size- Number of Scrips
	private int pfSize;

	// Scrip Screener Link Prefix
	private String screenerpf;

	// Scrip Screener Link Suffix
	private String screenersf;

	// Small Caps Market Cap Cr.
	private double mCapSmallCap;

	// Large Caps Market Cap Cr.
	private double mCapLargeCap;

	// T2T Series Name - BE
	private String t2tSeriesName;

	// Max'm PF lot size - Max'm amount needed to buy the whole PF once - in one lot
	private double maxpflotsize;

	// SMA for PF Exit to Compare CMP With in Number of Days
	private int smaExitDays;

	// Max Slot position for NFS
	private int nfsSlotMax;

	// SMA for PF Exit in case of Rank Fail to Compare CMP With in Number of Days
	private int smaExitRankFailDays;

	// Top Gun PF Size
	private int topGunPfSize;

	// Max Slot position for Top Gun
	private int topGunPfSlotMax;

	public EnumMCapClassification getMcapClassificationForMCapKCr(double Mcap)
	{
		EnumMCapClassification mcapEnum = null;

		if (Mcap > getMCapLargeCap())
		{
			mcapEnum = EnumMCapClassification.LargeCap;

		} else
		{
			if (Mcap < getMCapSmallCap())
			{
				mcapEnum = EnumMCapClassification.SmallCap;
			} else
			{
				mcapEnum = EnumMCapClassification.MidCap;
			}
		}
		return mcapEnum;
	}

}
