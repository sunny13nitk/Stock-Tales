package stocktales.IDS.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CorePFException extends RuntimeException
{

	public CorePFException(String message)
	{
		super(message);
		// TODO Auto-generated constructor stub
	}

}
