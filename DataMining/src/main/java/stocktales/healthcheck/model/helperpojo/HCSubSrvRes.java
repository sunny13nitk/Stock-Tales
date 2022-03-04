package stocktales.healthcheck.model.helperpojo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HCSubSrvRes
{
	private List<HCMsg>           messages   = new ArrayList<HCMsg>();
	private List<HCDataContainer> containers = new ArrayList<HCDataContainer>();
}
