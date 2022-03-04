package stocktales.IDS.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.NFS.enums.EnumMCapClassification;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScripMcapCatg
{
	private String scCode;
	private double mCapCr;
	private double alloc;
	private EnumMCapClassification mCapCatgName;
}
