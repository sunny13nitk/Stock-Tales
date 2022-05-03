package stocktales.ATH.model.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.NFS.model.entity.NFSRunTmp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ATHContainer
{
	private List<ATHPool> athPool = new ArrayList<ATHPool>();
	private List<String> invalidScrips = new ArrayList<String>();
	private List<ATHDeltas> momentumFltScrips = new ArrayList<ATHDeltas>();
	private List<NFSRunTmp> proposals = new ArrayList<NFSRunTmp>();
	private ATHStats athStats;
	private String message;

}
