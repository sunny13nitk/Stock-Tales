package stocktales.dataBook.helperPojo.scjournal.edit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScJEdit
{
	private long   id;
	private String scCode;
	private String prevTags;
	private String tag;
	private String tagOther;
	private String notes;
	
}
