package stocktales.IDS.model.pf.repo.intf;

import java.util.Date;

public interface IHCI_ScDatePrice
{
	String getSccode();

	Date getDate();

	Double getTxnppu();

}
