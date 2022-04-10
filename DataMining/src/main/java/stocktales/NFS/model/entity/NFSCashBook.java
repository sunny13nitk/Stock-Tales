package stocktales.NFS.model.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.NFS.enums.EnumNFSTxnType;

@Entity
@Table(name = "NFSCashBook")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSCashBook
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Enumerated(EnumType.STRING)
	private EnumNFSTxnType txntype;
	private Date date;
	private double amount;
	private double ddmax; // Max DD at the time of Txn. on PF - Analytics
	private double unrealzplper; // Unrealized P&L percentage at time of Txn - Analytics
	private double cash; // Current Cash Position - AS per Cash Flow Principles

}
