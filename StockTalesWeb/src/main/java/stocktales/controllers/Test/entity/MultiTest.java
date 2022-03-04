package stocktales.controllers.Test.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "testmulti")
public class MultiTest
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int    id;
	private String tag;
	private String catg;
}
