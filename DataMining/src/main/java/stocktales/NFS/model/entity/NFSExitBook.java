package stocktales.NFS.model.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "NFSExitBook")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSExitBook
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int exitid;

	private String sccode;

	private Date dateincl; // date of SCrip Inclusion in PF
	private double ppuincl; // price of Scrip Initial Inclusion in PF
	private double ppuavg; // Avg Holding PPU at time of Exit of Scrip over top ups
	private Date dateexit; // Date of Exit of Scrip
	private double ppuexit; // PPU at time of Exit
	private double realplper; // P&L realized in percentage
	private double realplperincl; // P&L realized as per ppuincl
	private int numdays; // Holding in number of Days
	private double realzamnt; // Actual Realized Amount
	private Date datelastbuy; // Latest top up date
	private double perpfexit; // Scrip as Percentage of PF at time of exit
}
