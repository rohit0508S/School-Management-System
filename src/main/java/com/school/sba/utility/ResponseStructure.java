package com.school.sba.utility;

import java.util.List;

import org.springframework.stereotype.Component;

import com.school.sba.entity.Schedule;
import com.school.sba.responsedto.AcademicProgramResponse;

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
