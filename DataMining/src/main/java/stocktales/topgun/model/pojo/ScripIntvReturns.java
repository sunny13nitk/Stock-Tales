package stocktales.topgun.model.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScripIntvReturns
{
	private String                sccode;
	private List<IntervalReturns> intvReturns = new ArrayList<IntervalReturns>();
}
