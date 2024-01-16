package com.school.sba.exception;

public class UserNotFoundByIdException extends RuntimeException{
	String message;

	public UserNotFoundByIdException(String message) {
		super();
		this.message = message;
	}
	@Override
	public String getMessage() {
		return message;
	}

}
