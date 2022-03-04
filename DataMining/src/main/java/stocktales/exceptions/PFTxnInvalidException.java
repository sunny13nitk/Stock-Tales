package stocktales.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * INValid PF Transaction Exception
 */
//The exception will trigger HTTP Status Code 400 
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class PFTxnInvalidException extends RuntimeException
{

	public PFTxnInvalidException(String message)
	{
		super(message);
	}

}
