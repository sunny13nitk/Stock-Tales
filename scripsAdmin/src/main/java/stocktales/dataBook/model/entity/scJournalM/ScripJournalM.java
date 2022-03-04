package stocktales.dataBook.model.entity.scJournalM;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.dataBook.enums.EnumEffect;
import stocktales.dataBook.enums.EnumSource;
import stocktales.dataBook.model.pojo.PJIntervalSimple;

@Entity
@Table(name = "scjournalM")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScripJournalM extends PJIntervalSimple
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private Date dateofentry;
	
	@NotNull
	private String sccode;
	
	@NotNull
	
	private String category;
	
	@NotNull
	private String tag;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private EnumSource source;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private EnumEffect effect;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "scripJorunal")
	private List<Snippet> snippets = new ArrayList<Snippet>();
	
	/*
	 * Utility Helper to Add new snippet to Scrip Journal
	 */
	public void addsnippet(
	        Snippet snippet
	)
	{
		if (this.snippets != null)
		{
			snippet.setScripJorunal(this);
			this.getSnippets().add(snippet);
		}
	}
	
}
