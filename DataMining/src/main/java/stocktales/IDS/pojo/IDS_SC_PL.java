package stocktales.IDS.pojo;

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
public class IDS_SC_PL
{
	private double nettPLAmount;
	private List<IDS_SC_PL_Items> plItems = new ArrayList<IDS_SC_PL_Items>();
}
