package com.matiasnicoletti;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionHandlerAdvice {

  private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

  /**
   * Maneja errores inesperados.
   *
   * @param exception exception a manejar
   * @return a serviceStatus with status 500
   */
  @ExceptionHandler({Exception.class})
  @ResponseBody
  public ResponseEntity<ErrorMessage> handleAnyException(Exception exception) {
    ErrorMessage message = new ErrorMessage();
    message.setError("500");
    message.setErrorDescription(exception.getMessage());
    logger.error("action=\"handleAnyException\" message=\"{}\" errorCode=\"{}\"",
                 message.getErrorDescription(), message.getError(), exception);
    return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Maneja errores de bd.
   * @param exception DataAccessException.
   * @return ErrorMessage.
   */
  @ExceptionHandler({DataAccessException.class})
  @ResponseBody
  public ResponseEntity<ErrorMessage> handleDataAccessException(DataAccessException exception) {
    ErrorMessage message = new ErrorMessage();
    message.setError("501");
    message.setErrorDescription(exception.getMessage());
    logger.error("action=\"handleDataAccessException\" message=\"{}\" errorCode=\"{}\"",
                 message.getErrorDescription(), message.getError(), exception);
    return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
