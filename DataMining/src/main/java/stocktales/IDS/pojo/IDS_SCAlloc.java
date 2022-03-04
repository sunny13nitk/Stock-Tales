package stocktales.IDS.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IDS_SCAlloc
{
	private String scCode;
	private String sector;
	private double idealAlloc;
	private double incAlloc;
}
