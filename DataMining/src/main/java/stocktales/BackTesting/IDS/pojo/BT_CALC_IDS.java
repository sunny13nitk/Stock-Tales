package stocktales.BackTesting.IDS.pojo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.IDS.model.pf.entity.HC;
import stocktales.IDS.model.pf.entity.HCI;
import stocktales.IDS.pojo.IDS_VPDetails;
import stocktales.NFS.model.pojo.NFSStockHistoricalQuote;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

/*
 * All Calculation(CALC) and Business Logic Stuff
 */
public class BT_CALC_IDS
{
	// PF Schema Session Replica
	private List<BT_PFSchema> pfSchema = new ArrayList<BT_PFSchema>();

	// Volatility Profile(s) Latest
	private List<IDS_VPDetails> volProfiles = new ArrayList<IDS_VPDetails>();

	// Holdings Header
	private List<HC> HC_IDS = new ArrayList<HC>();

	// Holdings Items
	private List<HCI> HCI_IDS = new ArrayList<HCI>();

	// Holdings Header
	private List<HC> HC = new ArrayList<HC>();

	// Holdings Items
	private List<HCI> HCI = new ArrayList<HCI>();

	// Prices Data Container for SMa Computations in Loop - Performance Feature
	private List<NFSStockHistoricalQuote> priceData = new ArrayList<NFSStockHistoricalQuote>();

	// Historic Prices Size for Scrips for Durations REconfiguration
	private List<BT_Sc_HPricesSize> scPHist = new ArrayList<BT_Sc_HPricesSize>();

	// Scrip with Minimum History which caused REconfiguration of Durations
	private BT_Sc_HPricesSize minHistoryScrip;

	// The Calendar Dates on Which the Market was open based on Min. History Scrip
	// History
	private List<Calendar> marketDays = new ArrayList<Calendar>();

}
