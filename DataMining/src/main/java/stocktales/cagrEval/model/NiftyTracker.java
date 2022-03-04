package stocktales.cagrEval.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "niftytracker")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NiftyTracker
{
	@Id
	private int    year;
	private double niftyvalue;
}
