package stocktales.healthcheck.model.helperpojo;

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
public class HCBeanReturn
{
	private double                valueToCmp;
	private List<HCMsg>           messages       = new ArrayList<HCMsg>();
	private List<HCDataContainer> dataContainers = new ArrayList<HCDataContainer>();
}
