package stocktales.siteconfig.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sitepaths")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SitePaths
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int    pathid;
	@NotNull
	private String title;
	@NotNull
	private String url;
	@Column(nullable = true)
	private String paramdesc;
	
	private boolean isadmin;
	@Column(nullable = true)
	private String  paramslistbean;
	
}
