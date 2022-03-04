package stocktales.NFS.model.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "NFSJournal")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSJournal
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int    jid;
	private Date   date;
	private int    numscrips;
	private double unrealpl;
	private double realpl;
	private int    numexits;
	private double perchurn;
	private double realplamnt;
	@Lob
	private String exits;
	@Lob
	private String entries;
}
