package stocktales.NFS.simulation.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "NFSTmpSM")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSRunTmpSM
{
	@Id
	private String sccode;
	private double consolscore;
	private int rank;
	private Date date;
}
