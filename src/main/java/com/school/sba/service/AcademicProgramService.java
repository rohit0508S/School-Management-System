package com.school.sba.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.AcademicProgramRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.utility.ResponseStructure;

public interface AcademicProgramService {

	ResponseEntity<ResponseStructure<AcademicProgramResponse>> addAcademicPrograms(int schoolId,
			AcademicProgramRequest programRequest);

	ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>> findAllAcademicPrograms(int schoolId);
	  public void deleteAcademicProgramPermanently();
	ResponseEntity<ResponseStructure<AcademicProgramResponse>> autoRepeatScheduleON(int programId,boolean autoRepeatScheduled);

	ResponseEntity<ResponseStructure<String>> deleteAcademicProgramById(int programId);

		
}
