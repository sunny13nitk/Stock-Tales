package stocktales.usersPF.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.usersPF.enums.EnumPFImpact;

@Entity
@Table(name = "userpfpldiv")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPF_PLDiv
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int plid;
	
	@Enumerated(EnumType.STRING)
	private EnumPFImpact type;
	
	private String sccode;
	
	private double amount;
	
	private String narration;
	
	@ManyToOne(cascade =
	{ CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "cid")
	private UserPFConfig userpfconfig;
	
}
