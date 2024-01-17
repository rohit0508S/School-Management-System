package com.school.sba.utility;

import org.springframework.stereotype.Component;

import com.school.sba.entity.Schedule;

import lombok.Getter;
import lombok.Setter;

@Component
@Setter
@Getter
public class ResponseStructure<T> {
	
	private int status;
	private String message;
	private T data;
	
	

}
