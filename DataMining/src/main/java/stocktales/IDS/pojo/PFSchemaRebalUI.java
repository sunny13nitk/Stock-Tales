package stocktales.IDS.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.IDS.enums.EnumMode;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PFSchemaRebalUI
{
	private String scripsStr = new String();
	private List<String> scrips = new ArrayList<String>();
	private IDS_ScAllocMassUpdate scAllocMassUpdate = new IDS_ScAllocMassUpdate();
	private EnumMode mode;
	private boolean isValidated;
	private PFSchemaRebalUIStats stats;

}
