package stocktales.NFS.model.ui;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.NFS.model.entity.NFSPF;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSUISCMassUpdateList
{
	private List<NFSPF> currPf = new ArrayList<NFSPF>();
}
