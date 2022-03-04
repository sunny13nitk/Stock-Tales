package stocktales.ExceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;
import stocktales.basket.allocations.exceptions.ExceptionNotFound;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHdlr
{
	/*
	 * Handle the Exception on the Controller Method
	 * - Handle for Particular Exception - 404
	 * - For a specific Case Only
	 */
	@ExceptionHandler(ExceptionNotFound.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ModelAndView handleNotFound(
	        Exception ex
	)
	{
		ModelAndView mv = new ModelAndView();
		mv.setViewName("errors/404Error");
		mv.addObject("message", ex.getMessage());
		
		return mv;
	}
}
