package stocktales.dataBook.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditSnippet
{
	private long   sid;
	private String tag;
	private String tagOther;
	private Byte[] image;
	private String notes;
	private String url;
}
