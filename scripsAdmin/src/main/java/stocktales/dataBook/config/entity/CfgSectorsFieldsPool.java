package stocktales.dataBook.config.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sec_fields_pool")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CfgSectorsFieldsPool
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int    fpid;
	@NotNull
	private String sector;
	@NotNull
	@Column(name = "f_pool_entityqname")
	private String fPoolEntityQName; //Qualified Entity Pojo for Field Pool
	
	@NotNull
	private String srvbean;
	
}
