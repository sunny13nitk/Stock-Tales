package stocktales.scripsEngine.uploadEngine.scripSheetServices.types;

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
public class ScripSectorsList
{
	private List<ScripSector> scripsSectors = new ArrayList<ScripSector>();
}
