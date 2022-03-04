package stocktales.topgun.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScripRetRank
{
	public String scCode;
	public double returns;
	public int    Rank;
}
