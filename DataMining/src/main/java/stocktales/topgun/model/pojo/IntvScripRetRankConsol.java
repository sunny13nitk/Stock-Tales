package stocktales.topgun.model.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IntvScripRetRankConsol
{
	private Date               endDate;
	private List<ScripRetRank> scripsRank = new ArrayList<ScripRetRank>();
	private double             eqWtRet;
	private double             retTopN;
	
}
