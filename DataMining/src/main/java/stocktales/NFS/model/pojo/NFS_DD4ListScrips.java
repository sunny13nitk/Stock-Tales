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
public class NFS_DD4ListScrips
{
	private double maxPerLoss;
	private List<NFS_DD4ListScripsI> scripDDItems = new ArrayList<NFS_DD4ListScripsI>();
}
