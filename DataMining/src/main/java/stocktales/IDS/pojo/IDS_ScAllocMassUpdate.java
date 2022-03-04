package stocktales.IDS.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.IDS.enums.EnumSchemaDepAmntsUpdateMode;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IDS_ScAllocMassUpdate
{
	private List<IDS_SCAlloc> scAllocList = new ArrayList<IDS_SCAlloc>();
	/**
	 * CurrDepAmnts - REfurbish Schema Deployable Amounts only considering Total
	 * Available Deployable Amounts; ignores already used amounts for current
	 * Holdings; takes current Schema deployment Amounts as base
	 * 
	 * Holistic - Consider (Used as well as current Deployable Amounts) SUM to
	 * calculate Total Corpus and distribute the amounts accordingly to Schema
	 * Deployable Amounts; More Holistic view of Corpus Deployments
	 * 
	 * None - Do not change current Deployment(s) in the Schema; Only for the new
	 * Money Bag Deposits/Withdrawals the new added Scrips to Schema deployments
	 * would change as per hence-forth Money addition withdrawals; the Status of
	 * deployment Amounts until now will remain status quo; ideal for slowly
	 * building new positions
	 */
	private EnumSchemaDepAmntsUpdateMode depAmtMode;

}
