package pet.store.controll.error;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalErrorHandler {
	
	public Map<String, String> handleNoSucElementException(NoSuchElementException ex) {
		log.error("Unable to find element: {}", ex.getMessage());
		Map<String, String> errorResponse =  new HashMap<>();
		errorResponse.put("message", ex.toString());
		return errorResponse;
	}

}
