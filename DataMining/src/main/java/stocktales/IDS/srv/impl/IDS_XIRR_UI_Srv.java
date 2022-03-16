package stocktales.IDS.srv.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.IDS.model.pf.repo.RepoHCI;
import stocktales.IDS.pojo.DateAmount;
import stocktales.IDS.pojo.XIRRContainer;
import stocktales.IDS.pojo.UI.IDS_XIRR_Chart;
import stocktales.IDS.pojo.UI.IDS_XIRR_Table;
import stocktales.IDS.pojo.UI.IDS_XIRR_UI;
import stocktales.IDS.srv.intf.IDS_PFDashBoardUISrv;
import stocktales.maths.UtilPercentages;
import stocktales.money.UtilDecimaltoMoneyString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Service()
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class IDS_XIRR_UI_Srv implements stocktales.IDS.srv.intf.IDS_XIRR_UI_Srv
{
	@Autowired
	private RepoHCI repoHCI;

	@Autowired
	private IDS_PFDashBoardUISrv pfDBSrv;

	private IDS_XIRR_UI xirrUI;

	@Override
	public IDS_XIRR_UI generateXIRRUI() throws Exception
	{
		this.xirrUI = null;
		XIRRContainer xirrCont = null;
		String dateSince = null;
		DateAmount currValTxn = null;
		List<DateAmount> XirrTxns = new ArrayList<DateAmount>();

		if (pfDBSrv != null && repoHCI != null)
		{
			xirrCont = pfDBSrv.getPFDashBoardContainer4mSession().getXirrContainer();
			XirrTxns = xirrCont.getTransactions().subList(0, xirrCont.getTransactions().size() - 1);
			currValTxn = xirrCont.getTransactions().get(xirrCont.getTransactions().size() - 1);

			dateSince = pfDBSrv.getPFDashBoardContainer4mSession().getStatsH().getInvSinceStr();

			xirrUI = new IDS_XIRR_UI();
			xirrUI.setNumBuys((int) XirrTxns.stream().filter(x -> x.getAmount() < 0).count());
			xirrUI.setNumBuyTxns(repoHCI.getCountBuyTxns());
			xirrUI.setNumSells((int) XirrTxns.stream().filter(x -> x.getAmount() > 0).count());
			xirrUI.setNumSellTxns(repoHCI.getCountSellTxns());
			xirrUI.setSince(dateSince);
			xirrUI.setXirr(xirrCont.getXirr());
			xirrUI.setAbspl(pfDBSrv.getPFDashBoardContainer4mSession().getStatsH().getPfPLSS().getPerPL());

			/*
			 * --- Prepare XIRR Table & Chart-------
			 */

			double totalInv = xirrCont.getTransactions().stream().filter(x -> x.getAmount() < 0)
					.mapToDouble(DateAmount::getAmount).sum();
			if (totalInv < 0) // -ve Cash Flows
			{
				SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
				SimpleDateFormat formatterNum = new SimpleDateFormat("dd/MM/yyyy");

				for (DateAmount txn : XirrTxns)
				{
					IDS_XIRR_Chart xirrChart = new IDS_XIRR_Chart();
					xirrChart.setDate(formatterNum.format(txn.getDate()));
					if (txn.getAmount() < 0)
					{
						xirrUI.getXirrTable()
								.add(new IDS_XIRR_Table(txn.getDate(), formatter.format(txn.getDate()),
										Precision.round(txn.getAmount(), 1),
										UtilDecimaltoMoneyString.getMoneyStringforDecimal(txn.getAmount() * -1, 2),
										UtilPercentages.getProportion(totalInv, txn.getAmount(), 1)));

						xirrChart.setInv(Precision.round(txn.getAmount() * -1, 0));
						xirrChart.setSales(0);
						xirrChart.setValue(0);
					} else
					{
						xirrUI.getXirrTable()
								.add(new IDS_XIRR_Table(txn.getDate(), formatterNum.format(txn.getDate()),
										txn.getAmount(),
										UtilDecimaltoMoneyString.getMoneyStringforDecimal(txn.getAmount() * -1, 2), 0));
						xirrChart.setInv(0);
						xirrChart.setSales(Precision.round(txn.getAmount() * -1, 0));
						xirrChart.setValue(0);
					}

					xirrUI.getXirrChart().add(xirrChart);

				}
				// Add Last Row for Current Value
				xirrUI.getXirrTable()
						.add(new IDS_XIRR_Table(currValTxn.getDate(), formatter.format(currValTxn.getDate()),
								Precision.round(currValTxn.getAmount(), 1),
								UtilDecimaltoMoneyString.getMoneyStringforDecimal(currValTxn.getAmount(), 2), 0));
				xirrUI.getXirrChart().add(
						new IDS_XIRR_Chart(formatterNum.format(currValTxn.getDate()), 0, 0, currValTxn.getAmount()));
			}

		}

		xirrUI.getXirrTable().sort(Comparator.comparing(IDS_XIRR_Table::getDate).reversed());
		return this.xirrUI;
	}

}
