package stocktales.basket.allocations.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ResponseStatus(value = HttpStatus.NOT_FOUND) //Triggered as 404
public class ExceptionNotFound extends RuntimeException
{
	private String message;
	
}
