package com.school.sba.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestdto.SchoolRequest;
import com.school.sba.responsedto.SchoolResponse;
import com.school.sba.service.SchoolService;
import com.school.sba.utility.ResponseStructure;



@RestController
public class SchoolController {
	@Autowired
	private SchoolService schoolService;
	@PostMapping("/schools")
	public ResponseEntity<ResponseStructure<SchoolResponse>> registerSchool(@RequestBody SchoolRequest schoolRequest)
	{
		return schoolService.registerSchool(schoolRequest);
	}
	@DeleteMapping("schools/{schoolId}")
	public ResponseEntity<ResponseStructure<String>> deleteSchoolById(@PathVariable int schoolId)
	{
		return schoolService.deleteSchoolById(schoolId);
	}
}
