package stocktales.IDS.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MCapSpread
{
	private String mCapCatgName;
	private int numScrips;
	private double alloc;
}
