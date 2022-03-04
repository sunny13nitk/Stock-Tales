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
@Table(name = "NFSPF")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSPF
{
	@Id
	private String sccode;
	private int    rankincl;
	private double priceincl;
	private int    rankcurr;
	private Date   dateincl;
	private Date   datelasttxn;
	private int    units;
	
}
