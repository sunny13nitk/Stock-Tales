package stocktales.IDS.model.pf.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PFSchema")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PFSchema
{
	@Id
	private String sccode;
	private String sector;
	private double idealalloc;
	private double incalloc;
	private double depamnt;
}
