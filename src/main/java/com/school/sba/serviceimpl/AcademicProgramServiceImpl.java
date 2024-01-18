package com.school.sba.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.exception.SchoolNotFoundByIdException;
import com.school.sba.repositary.AcademicProgramRepository;
import com.school.sba.repositary.SchoolRepositary;
import com.school.sba.requestdto.AcademicProgramRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.service.AcademicProgramService;
import com.school.sba.utility.ResponseStructure;

@Service
public class AcademicProgramServiceImpl implements AcademicProgramService {
	@Autowired
	private AcademicProgramRepository academicProgramRepository;
	@Autowired
	private SchoolRepositary schoolRepositary;
	@Autowired
	private ResponseStructure<AcademicProgramResponse> responseStructure;

	
	private AcademicProgram mapToAcademicProgram(AcademicProgramRequest academicProgramRequest) {
		return AcademicProgram.builder()
				.programId(academicProgramRequest.getProgramId())
				.programName(academicProgramRequest.getProgramName())
				.beginsAt(academicProgramRequest.getBeginsAt())
				.endsAt(academicProgramRequest.getEndsAt())
				.build();
	}
	private AcademicProgramResponse mapToAcademicProgramResponse(AcademicProgram academicProgram)
	{
		return AcademicProgramResponse.builder()
				.programName(academicProgram.getProgramName())
				.beginsAt(academicProgram.getBeginsAt())
				.endsAt(academicProgram.getEndsAt())
				.build();
	}
		
@Override
public ResponseEntity<ResponseStructure<AcademicProgramResponse>> createAcademicProgram(int schoolId,
		AcademicProgramRequest academicProgramRequest) {
		  return schoolRepositary.findById(schoolId).map(school -> {
	            AcademicProgram academicProgram = mapToAcademicProgram(academicProgramRequest);
	            academicProgramRepository.save(academicProgram);	            
	            academicProgram.setSchool(school);  
	            AcademicProgram savedProgram = academicProgramRepository.save(academicProgram);
	            AcademicProgramResponse response = mapToAcademicProgramResponse(savedProgram);
	            responseStructure.setStatus(HttpStatus.CREATED.value());
	            responseStructure.setMessage("Academic program created successfully ...!");
	            responseStructure.setData(response);           
//	            return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(responseStructure, HttpStatus.CREATED);
//		  }).orElseThrow(()->new SchoolNotFoundByIdException("School not found...!"));
	    
	            
	            return new ResponseEntity<>(responseStructure, HttpStatus.CREATED);
		    }).orElseThrow(() -> new SchoolNotFoundByIdException("School not found...!"));

	}

}
