package stocktales.IDS.srv.intf;

import stocktales.IDS.model.pf.entity.MoneyBag;

public interface IDS_MoneyBagSrv
{
	public void processMBagTxn(MoneyBag mBagTxn);

	/**
	 * Get Deployable AMount - Scope Money Bag [Only Considers Deposits + Dividends
	 * - Withdrawals from Money Bag]
	 * 
	 * @return - Amount
	 */
	public double getDeployableAmount();

	/**
	 * Get Deployable AMount - Scope Money Bag & current Holdings [Considers
	 * Deposits + Dividends - Withdrawals from Money Bag - Sum of Current Holdings
	 * Amounts]
	 * 
	 * Note: Not to be used in evaluations where there are sales and purchases in
	 * tandem as sales might not be triggered first than purchases to increment the
	 * Money Available for purchases
	 * 
	 * @return - Amount
	 */
	public double getDeployableAmountIncHoldings();

	/**
	 * Validate Multiple Purchase Transaction amounts as a Sum against the Amount
	 * available in MoneyBag
	 * 
	 * @param sumTxnAmounts - Sum of all the Purchase transaction Amounts
	 * @return - Boolean ( true in case of Valid)
	 */
	public boolean validateMassPurchaseTxnsAmount(double sumTxnAmounts);
}
