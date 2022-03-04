package stocktales.dataBook.helperPojo.scjournal.dbproc;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NumandLastEntry
{
	private long numEntries;
	private Date lastEntryDate;
}
