package stocktales.dataBook.model.entity.scripSp;

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
import stocktales.dataBook.enums.EnumUnit;
import stocktales.dataBook.model.pojo.PJIntervalSimple;

@Entity
@Table(name = "FPBAJFINANCE")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FPBAJFINANCE extends PJIntervalSimple
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long     id;
	@Enumerated(EnumType.STRING)
	private EnumUnit unit;
	private double   bhflaum;         //AUM BHFL
	private double   bhflopextonii;   //BHFL OPEX TO NII
	private double   bhflhomeloanaum; //BHFL Home loans AUM
	private double   bhfllapaum;
	private double   bhfllrdaum;
	private double   bhfldevfinaum;
	private double   bhflruralaum;
	private double   bhflcar;         //Capital Adequacy Ratio- BHFL
	private double   bhflnnpa;
	private double   bhflgnpa;
	
	private double hltosalaried;
	private double hltoselfemp;
	private double hltoprof;
	private double hllocations;     //Locations from which HL offered
	private double laplocations;    //Locations from which LAP offered
	private double rurallocations;  //Locations from which HL and LAP offered to rural .
	private double devfinlocations; //Locations from which Dev financing offered
	
	private double bfslsales;
	private double bfslpat;    //BFSL PAT  
	private int    totalloc;   //Total Branches
	private int    ruralloc;   //Rural Branches
	private double posl;       //Point of Sales in Lakhs
	private double emicardm;   //Digital EMI cards  CIF in M
	private double coemicardm; //Co Branded EMI Cards in M
	private double mobikwikm;  //Mobikwik users in M
	
	private double cftotalm;           //Total Customer Franchise
	private double xsellm;             // xSell franchise
	private double bestm;              //Non delinquent best customers
	private double perbestxsell;       //Percentage Best Cross Sell of Total Franchise
	private double newcustomersm;      //New Customers Onboarded
	private double newloansbookedm;    //New Loans Booked
	private double newloanstoexiscust; //New Loans to Existing customers as %
	
}
