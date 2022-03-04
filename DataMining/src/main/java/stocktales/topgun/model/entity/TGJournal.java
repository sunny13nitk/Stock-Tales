package stocktales.topgun.model.entity;

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
@Table(name = "TGJournal")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TGJournal
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int    jid;
	private Date   date;          //End Date - Trigger Date
	private double allequwret;
	private double topnrealized;
	private double topnunrealized;
	private int    numexits;
	private double perchurn;
	@Lob
	private String scripspresent;
	@Lob
	private String scripsexited;
}
