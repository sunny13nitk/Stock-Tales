package stocktales.controllers.Test.pojo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class TestMulti
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int    id;
	private String tag;
	private String catg;
	private String othertag;
}
