package stocktales.DataLake.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UploadStats
{
	private long numScrips;
	private long numEntries;
	private long numErrors;
}
