package stocktales.NFS.model.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bsedataset")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BseDataSet
{
	@Id
	public String nsecode;
	public String name;
	public String series;
	
}
