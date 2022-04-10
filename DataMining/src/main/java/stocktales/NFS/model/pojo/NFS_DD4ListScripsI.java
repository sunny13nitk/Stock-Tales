package stocktales.NFS.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFS_DD4ListScripsI extends NFSExitSMADelta
{
	private String scCode;
	private double wtdPLPer;

}
