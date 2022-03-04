package stocktales.IDS.srv.impl;

import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.IDS.model.pf.entity.HC;
import stocktales.IDS.model.pf.entity.HCI;
import stocktales.IDS.model.pf.repo.RepoHC;
import stocktales.IDS.model.pf.repo.RepoPFSchema;
import stocktales.IDS.srv.intf.IDS_MoneyBagSrv;
import stocktales.IDS.srv.intf.IDS_PFTxn_Validator;
import stocktales.exceptions.PFTxnInvalidException;
import stocktales.usersPF.enums.EnumTxnType;

@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IDS_PFTxn_ValidSrv implements IDS_PFTxn_Validator
{
	@Autowired
	private IDS_MoneyBagSrv mbSrv;

	@Autowired
	private MessageSource msgSrc;

	@Autowired
	private RepoPFSchema repoPFSchema;

	@Autowired
	private RepoHC repoHC;

	@Override
	public boolean isTxnValid(HCI pfTxn)
	{
		boolean isValid = false;

		if (pfTxn != null)
		{
			if (pfTxn.getUnits() > 0 || pfTxn.getTxntype() == EnumTxnType.Exit) // At lest some units transacted
			{

				switch (pfTxn.getTxntype()) // based on Txn Type
				{
				case Buy:

					double txnAmnt = pfTxn.getUnits() * pfTxn.getTxnppu();
					double scDEpAmnt = 0;
					double mbAmnt = 0;

					if (txnAmnt > 0)
					{

						mbAmnt = mbSrv.getDeployableAmount();
						// Paisa hona chaiye - pehli baat
						if (mbAmnt > 0 && mbAmnt >= txnAmnt)
						{
							// Scrip ke khud ke pass mei paisa hona chahiye - doosri baat
							scDEpAmnt = repoPFSchema.getDeployableAmountforScrip(pfTxn.getSccode());
							if (scDEpAmnt > 0 && scDEpAmnt >= txnAmnt)
							{
								isValid = true;
							} else
							{
								// Trigger Custom Exception
								throw new PFTxnInvalidException(msgSrc.getMessage("pfTxn.depAmnt", new Object[]
								{ scDEpAmnt, txnAmnt }, Locale.ENGLISH));
							}
						} else
						{
							// Trigger Custom Exception
							throw new PFTxnInvalidException(msgSrc.getMessage("pfTxn.mbAmnt", new Object[]
							{ mbAmnt, txnAmnt }, Locale.ENGLISH));
						}

					}
					break;

				case Sell:
					// Get Scrip Holding from HC

					if (repoHC != null)
					{
						Optional<HC> hcO = repoHC.findById(pfTxn.getSccode());
						if (hcO.isPresent())
						{
							if (hcO.get().getUnits() >= pfTxn.getUnits())
							{
								isValid = true;
							}
						}
					}
					break;

				case Exit:
					if (repoHC != null)
					{
						Optional<HC> hcO = repoHC.findById(pfTxn.getSccode());
						if (hcO.isPresent())
						{
							if (hcO.get().getUnits() >= pfTxn.getUnits())
							{
								isValid = true;
							}
						} else
						{
							isValid = true;
						}
					}

					break;
				}
			}
		}

		return isValid;

	}

}
