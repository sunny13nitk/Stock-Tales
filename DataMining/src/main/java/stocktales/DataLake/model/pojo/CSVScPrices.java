package stocktales.DataLake.model.pojo;

import java.util.Date;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CSVScPrices
{
	@CsvBindByPosition(position = 0)
	@CsvDate(value = "yyyy-MM-dd")
	private Date date;
	@CsvBindByPosition(position = 1)
	private double open;
	@CsvBindByPosition(position = 2)
	private double high;
	@CsvBindByPosition(position = 3)
	private double low;
	@CsvBindByPosition(position = 4)
	private double close;
	@CsvBindByPosition(position = 5)
	private double adjclose;
	@CsvBindByPosition(position = 6)
	private long volume;

	@Override
	public String toString()
	{
		return "CSVScPrices [date=" + date + ", open=" + open + ", high=" + high + ", low=" + low + ", close=" + close
				+ ", adjclose=" + adjclose + ", volume=" + volume + "]";
	}

}
