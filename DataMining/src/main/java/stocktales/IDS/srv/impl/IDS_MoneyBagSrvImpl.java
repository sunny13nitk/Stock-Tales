package stocktales.IDS.srv.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.IDS.events.EV_MoneyBagTxn;
import stocktales.IDS.model.pf.entity.MoneyBag;
import stocktales.IDS.model.pf.repo.RepoHC;
import stocktales.IDS.model.pf.repo.RepoMoneyBag;
import stocktales.IDS.srv.intf.IDS_MoneyBagSrv;

@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IDS_MoneyBagSrvImpl implements IDS_MoneyBagSrv
{
	@Autowired
	private RepoMoneyBag repoMoneyBag;

	@Autowired
	private RepoHC repoHC;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@Override
	public void processMBagTxn(MoneyBag mBagTxn)
	{
		// Delegate To Event Publisher Method of Service
		publishMoneyBagTxnEvent(mBagTxn);
	}

	public void publishMoneyBagTxnEvent(MoneyBag mBagTxn)
	{
		// Create an Instance of Money Bag Transaction Event
		EV_MoneyBagTxn evMB = new EV_MoneyBagTxn(this, mBagTxn);
		// Publish the Event using Injected Application Event Publisher
		applicationEventPublisher.publishEvent(evMB);

	}

	@Override
	public double getDeployableAmount()
	{
		double amnt = 0;
		if (repoMoneyBag != null)
		{

			double depAmnt;
			try
			{
				depAmnt = repoMoneyBag.getDepositTxnTotal();
			} catch (Exception e)
			{
				depAmnt = 0;
			}

			double wdwAmnt;
			try
			{
				wdwAmnt = repoMoneyBag.getWithdrawalTxnTotal();
			} catch (Exception e)
			{
				wdwAmnt = 0;
			}

			double divAmnt;
			try
			{
				divAmnt = repoMoneyBag.getDividendTxnTotal();
			} catch (Exception e)
			{
				divAmnt = 0;
			}

			amnt = depAmnt + divAmnt - wdwAmnt;
		}
		return amnt;
	}

	@Override
	public double getDeployableAmountIncHoldings()
	{
		double Amnt = this.getDeployableAmount();
		if (repoHC != null)
		{
			if (repoHC.count() > 0)
			{
				Amnt -= repoHC.getTotalInvestments();
			}
		}

		return Amnt;

	}

	@Override
	public boolean validateMassPurchaseTxnsAmount(double sumTxnAmounts)
	{
		boolean isValid = false;

		if (sumTxnAmounts < getDeployableAmountIncHoldings())
		{
			isValid = true;
		}

		return isValid;
	}

}
