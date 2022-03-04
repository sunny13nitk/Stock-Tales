package stocktales.NFS.model.pojo;

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
public class NFSPriceManipulation
{
	private String                          scCode;
	private List<NFSPriceManipulationItems> priceItems = new ArrayList<NFSPriceManipulationItems>();
}
