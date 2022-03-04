package stocktales.NFS.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSSmaCmp
{
	
	private String scCode;
	private double cmp;
	private double sma20;
	private double sma50;
	private double sma100;
	
	private double cmpSma20Delta;
	private double cmpSma100Delta;
	
}
