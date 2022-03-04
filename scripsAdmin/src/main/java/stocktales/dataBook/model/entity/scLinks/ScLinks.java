package stocktales.dataBook.model.entity.scLinks;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.URL;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ScripLinks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScLinks
{
	@Id
	private String sccode;
	
	@URL
	private String urlcompany;
	
	@URL
	private String urlscreener;
	
	@URL
	private String urlnse;
	
	@URL
	private String urltrendlyne;
	
	@URL
	private String urlvp;
}
