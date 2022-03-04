package stocktales.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND) // 404 HTTP
public class SchemaUpdateException extends RuntimeException
{
	public SchemaUpdateException(String message)
	{
		super(message);
	}
}
