package stocktales.healthcheck.model.helperpojo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.healthcheck.beanSrv.helperPOJO.NameVal;
import stocktales.healthcheck.enums.EnumDataContainerTitle;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HCDataContainer
{
	private EnumDataContainerTitle title;
	private List<NameVal>          nameValContainer = new ArrayList<NameVal>();
}
