package stocktales.IDS.pojo.UI;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.IDS.model.pf.entity.HCI;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IDSBuyPropMassUpdateList
{
	private List<HCI> buyList = new ArrayList<HCI>();
}
