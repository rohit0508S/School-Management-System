package com.school.sba.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Subject;
import com.school.sba.exception.AcademicProgramNotFoundByIdException;
import com.school.sba.repositary.AcademicProgramRepository;
import com.school.sba.repositary.SubjectRepository;
import com.school.sba.requestdto.SubjectRequestDto;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.service.SubjectService;
import com.school.sba.utility.ResponseStructure;
@Service
public class SubjectServiceImpl implements SubjectService{
	@Autowired
	private AcademicProgramRepository academicRepo;
	@Autowired
	private SubjectRepository subjectRepo;
	@Autowired
	private AcademicProgramServiceImpl academicProgramServiceImpl;
	@Autowired
	private ResponseStructure<AcademicProgramResponse> structure;

	
    public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addSubjects(SubjectRequestDto subjectRequest,int programId) {
        
        return academicRepo.findById(programId).map(program -> {
            List<Subject> subjects = new ArrayList<Subject>();
            subjectRequest.getSubjectName().forEach(name -> {
                Subject subject = subjectRepo.findBySubjectName(name).map(s->s).orElseGet(() -> {
                    Subject newSubject = new Subject();
                    newSubject.setSubjectName(name);
                    subjectRepo.save(newSubject);
                    return newSubject;
                });
                subjects.add(subject);
            });
            program.setSubjects(subjects);
            academicRepo.save(program);

            structure.setStatus(HttpStatus.CREATED.value());
            structure.setMessage("Added subject list to Academic-Program");
            structure.setData(academicProgramServiceImpl.mapToAcademicProgramResponse(program));
            return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure,
                    HttpStatus.CREATED);
        }).orElseThrow(() -> new AcademicProgramNotFoundByIdException("Given programId not present in the database"));
    }
	 
		
	

	

}
