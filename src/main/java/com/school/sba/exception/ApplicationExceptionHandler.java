package com.school.sba.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.school.sba.utility.ErrorStructre;

@RestControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {
	
	@Autowired
	private ErrorStructre<String> errstructure;
	
	@ExceptionHandler(SchoolNotFoundByIdException.class)
	public ResponseEntity<ErrorStructre<String>> schoolNotFoundById(SchoolNotFoundByIdException e)
	{
		
		
		errstructure.setStatus(HttpStatus.NOT_FOUND.value());
		errstructure.setMessage(e.getMessage());
		errstructure.setRootCause("School Data Not Present for Given School Id");
		
		return new ResponseEntity<ErrorStructre<String>>(errstructure,HttpStatus.NOT_FOUND);
		
	}
	
	@ExceptionHandler(SchoolDataNotFoundException.class)
	public ResponseEntity<ErrorStructre<String>> schoolsNotFound(SchoolDataNotFoundException e)
	{
		
		
		errstructure.setStatus(HttpStatus.NOT_FOUND.value());
		errstructure.setMessage(e.getMessage());
		errstructure.setRootCause("School Data Not Present To fetch school details");
		
		return new ResponseEntity<ErrorStructre<String>>(errstructure,HttpStatus.NOT_FOUND);
		
	}
	
	
	 @ExceptionHandler(DataNotFoundException.class)
	    public ResponseEntity<ErrorStructre<String>> handleRuntime(DataNotFoundException exception) {
	        return new ResponseEntity<>(errstructure(HttpStatus.BAD_REQUEST, exception.getMessage(), "Illegal Argument"),
	                HttpStatus.BAD_REQUEST);
	    }

	    private ErrorStructre<String> errstructure(HttpStatus status, String message, String rootCause) {
	        ErrorStructre<String> errorStructure = new ErrorStructre<>();
	        errorStructure.setStatus(status.value());
	        errorStructure.setMessage(message);
	        errorStructure.setRootCause(rootCause);
	        return errorStructure;
	    }
	}
	
	

