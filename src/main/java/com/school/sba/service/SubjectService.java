package com.school.sba.service;

import org.springframework.http.ResponseEntity;
import com.school.sba.requestdto.SubjectRequestDto;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.utility.ResponseStructure;

public interface SubjectService {

	ResponseEntity<ResponseStructure<AcademicProgramResponse>> addSubjects(SubjectRequestDto subjectRequestDto,
			int programId);

}
