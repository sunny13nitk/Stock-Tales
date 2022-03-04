package stocktales.dataBook.model.entity.scJournalM;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.URL;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "snippet")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Snippet
{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long sid;
	
	@Lob
	private String notes;
	
	@Lob
	private Byte[] image;
	
	@URL
	private String url;
	
	@ManyToOne(cascade =
	{ CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "id")
	private ScripJournalM scripJorunal;
}
