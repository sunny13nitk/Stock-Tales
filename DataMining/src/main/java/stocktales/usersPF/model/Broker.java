package stocktales.usersPF.model;

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
@Table(name = "Brokers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Broker
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String brokercode;
	
	private double brokerage;
	
	private double minmbrokerage;
	
	private double stt;
	
	private double txncharges;
	
	private double gst;
	
	private double sebi;
}
