package stocktales.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class StockQuoteException extends RuntimeException
{

	public StockQuoteException(String arg0)
	{
		super(arg0);

	}

}
