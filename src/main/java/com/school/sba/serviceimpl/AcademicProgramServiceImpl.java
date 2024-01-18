package com.school.sba.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.school.sba.entity.AcademicProgram;
import com.school.sba.exception.DataNotFoundException;
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
		List<String> subjectNames=new ArrayList<String>();
		academicProgram.getSubjects().forEach(subject->{
			subjectNames.add(subject.get);
		});
		
		return AcademicProgramResponse.builder()
				.programName(academicProgram.getProgramName())
				.beginsAt(academicProgram.getBeginsAt())
				.endsAt(academicProgram.getEndsAt())
				.subject(subjectNames)
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
	            return new ResponseEntity<>(responseStructure, HttpStatus.CREATED);
		    }).orElseThrow(() -> new SchoolNotFoundByIdException("School not found...!"));

	}

//@Override
//public ResponseEntity<ResponseStructure<AcademicProgramResponse>> findAllAcademicProgram(int schoolId) {
//    List<AcademicProgram> academicPrograms = academicProgramRepository.findAll();
//    
//    if (academicPrograms.isEmpty()) {
//        responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
//        responseStructure.setMessage("No academic programs found for the given school ID");
//        responseStructure.setData(null);
//    } else {
//    	
//    	
//        AcademicProgramResponse response = mapToAcademicProgramResponse(academicPrograms.get(schoolId));
//        
//        responseStructure.setStatus(HttpStatus.OK.value());
//        responseStructure.setMessage("Academic program retrieved successfully");
//        responseStructure.setData(response);
//    }
//
//    return new ResponseEntity<>(responseStructure, HttpStatus.OK);
//}
@Override
public ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>> findAllAcademicProgram(int schoolId) {
	return schoolRepositary.findById(schoolId).map(school -> {

		List<AcademicProgram> programs = academicProgramRepository.findAll();

		if (!programs.isEmpty()) {
			List<AcademicProgramResponse> list = new ArrayList<>();

			for (AcademicProgram program : programs) {
				AcademicProgramResponse response = mapToAcademicProgramResponse(program);
				list.add(response);
			}
			ResponseStructure<List<AcademicProgramResponse>> structure = new ResponseStructure<>();
			structure.setStatus(HttpStatus.FOUND.value());
			structure.setMessage("Here is the list Academic-Progrms of our SCHOOL");
			structure.setData(list);
			return new ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>>(structure,
					HttpStatus.FOUND);
		} else
			throw new DataNotFoundException("No Academic Programs present for given school");

	}).orElseThrow(() -> new SchoolNotFoundByIdException("School Not Present for given school id"));
}
}
