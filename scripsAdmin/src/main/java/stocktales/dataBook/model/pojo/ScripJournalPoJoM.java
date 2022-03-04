package stocktales.dataBook.model.pojo;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.dataBook.enums.EnumEffect;
import stocktales.dataBook.enums.EnumSource;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScripJournalPoJoM extends PJIntervalSimple
{
	private String sccode;
	
	private String category;
	
	private String catgOther;
	
	private String tag;
	
	private String tagOther;
	
	@Enumerated(EnumType.STRING)
	private EnumSource source;
	@Enumerated(EnumType.STRING)
	private EnumEffect effect;
	
	private String url;
	
}
