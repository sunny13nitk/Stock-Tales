package stocktales.healthcheck.model.entity;

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
import stocktales.dataBook.enums.EnumEffect;
import stocktales.dataBook.enums.EnumEmphasis;
import stocktales.healthcheck.enums.EnumHCCriteria;
import stocktales.healthcheck.enums.EnumSign;

@Entity
@Table(name = "hcconfig")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cfg_ScripHealthCheck
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int            id;
	@Enumerated(EnumType.STRING)
	private EnumHCCriteria stext;
	private String         srvname;
	@Enumerated(EnumType.STRING)
	private EnumSign       cmpsign;
	private double         baseval;
	@Enumerated(EnumType.STRING)
	private EnumEffect     effect;
	@Enumerated(EnumType.STRING)
	private EnumEmphasis   emphasis;
	private String         msgkey;        //HCMessages.properties
	private String         repkey;        //HCRep.properties
	private String         tagkey;        //HCTags.properties
	private Boolean        forFinancials; //Relevant for Financials;
	
}
