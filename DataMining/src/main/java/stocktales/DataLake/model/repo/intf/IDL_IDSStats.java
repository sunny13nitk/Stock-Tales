package stocktales.DataLake.model.repo.intf;

import java.util.Date;

public interface IDL_IDSStats
{
	String getSccode(); // scCode

	Date getMindate(); // from

	Date getMaxdate(); // to

	Long getNumentries(); // Number of Entries

}
