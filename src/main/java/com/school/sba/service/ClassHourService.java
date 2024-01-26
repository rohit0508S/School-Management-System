package com.school.sba.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import com.school.sba.utility.ResponseStructure;

public interface ClassHourService {

	
	ResponseEntity<ResponseStructure<String>> addClassHour(int programId);

}
