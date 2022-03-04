package stocktales.helperPOJO;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.basket.allocations.autoAllocation.facades.pojos.SC_EDRC_Summary;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SCEDRCSumList
{
	private List<SC_EDRC_Summary> scEDRCSumList = new ArrayList<SC_EDRC_Summary>();
}
