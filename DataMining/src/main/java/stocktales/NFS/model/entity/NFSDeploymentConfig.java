package stocktales.NFS.model.entity;

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
@Table(name = "NFSDepConfig")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSDeploymentConfig
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int slabid;
	private double ddmin; // Drawdown Min'n for Slab
	private double ddmax; // Drawdown Max'm for Slab

}
