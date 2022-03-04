package stocktales.NFS.model.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "NFSTmp")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSRunTmp
{
	@Id
	private String sccode;
	private double consolscore;
	private int    rank;
	private Date   date;
}
