package stocktales.dataBook.model.entity.scjournal;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.dataBook.enums.EnumEffect;
import stocktales.dataBook.enums.EnumSource;
import stocktales.dataBook.model.pojo.PJInterval;

@Entity
@Table(name = "scjournal")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScripJournal extends PJInterval
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
	
	private String url;
	
	@Lob
	private Byte[] image;
	
}
