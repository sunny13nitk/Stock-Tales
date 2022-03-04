package stocktales.dataBook.model.pojo;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.dataBook.enums.EnumInterval;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class PJInterval
{
	@Enumerated(EnumType.STRING)
	@Column
	private EnumInterval interval; //Store as String
	
	@Column
	private int valm; //Main Value e.g. 2020 - for Year
	
	@Column
	private int vald; //Details Value e.g. 3 - for Quarter 3
	
	@Column
	@Lob
	private String notes; //Notes for Any Comments for Particular Interval
	
}
