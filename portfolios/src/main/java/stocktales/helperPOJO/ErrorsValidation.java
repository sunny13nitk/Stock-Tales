package stocktales.helperPOJO;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorsValidation
{
	private boolean      isError;
	private List<String> messages = new ArrayList<String>();
}
