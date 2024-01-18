package com.school.sba.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.Subject;
import com.school.sba.repositary.AcademicProgramRepository;
import com.school.sba.repositary.SubjectRepository;
import com.school.sba.requestdto.AcademicProgramRequest;
import com.school.sba.requestdto.SubjectRequestDto;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.service.SubjectService;
import com.school.sba.utility.ResponseStructure;

public class SubjectServiceImpl implements SubjectService {
	
	
	@Autowired
	SubjectRepository subjectRepo;
	@Autowired
	AcademicProgramRepository academicRepo;
	
	private AcademicProgram maptoAcademicProgram(AcademicProgramRequest academicProgramRequest) {
		return AcademicProgram.builder()
				.programId(academicProgramRequest.getProgramId())
				.programName(academicProgramRequest.getProgramName())
				.beginsAt(academicProgramRequest.getBeginsAt())
				.endsAt(academicProgramRequest.getEndsAt())
				.build();
	}
	public AcademicProgramResponse mapAcademicProgramResponse(AcademicProgram academicProgram) {
		List<String> subjectNames=new ArrayList<String>();
		academicProgram.getSubjects(s)
		
		
	}
	
	
	
	
	
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addSubject(SubjectRequestDto subjectRequestDto,int programId)
	{		
		return academicRepo.findById(programId).map(program->{
			List<Subject> subjects=new ArrayList<Subject>();
			subjectRequestDto.getSubjectame().forEach(name->{
				subjectRepo.findBySubjectName(name).map(subject)->{
			subjects.add(subject);
					return null;					
				}).orElseGet(()->{
		    Subject subject=new Subject();
		    subject.setSubjectName(name);
		    subjectRepo.save(subject);
		    subjects.add(subject);
		    return null;
	        });
			});
		    program.setSubjects(subject);
		    structure.setStatus();
		    structure.setMassage();
		    structure.setData(academicProgramServiceImpl);
	
	}
}
}