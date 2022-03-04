package stocktales.topgun.model.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "DataPool")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DataPool
{
	@Id
	private String nsecode;

	// Rank when Included - Latest
	private int rank;

}
