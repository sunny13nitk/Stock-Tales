package stocktales.NFS.model.ui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.NFS.model.entity.NFSRunTmp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSRunTmp_UISel extends NFSRunTmp
{
	private String screenerUrl;
	private boolean isincluded;
}
