package br.com.robson.robssohex.transportlayers;

import br.com.robson.robssohex.model.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Error> handleIllegalArgument(IllegalArgumentException ex) {
        Error err = new Error();
        err.setCode("BAD_REQUEST");
        err.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Error> handleIllegalState(IllegalStateException ex) {
        Error err = new Error();
        err.setCode("GONE");
        err.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.GONE).body(err);
    }
}

