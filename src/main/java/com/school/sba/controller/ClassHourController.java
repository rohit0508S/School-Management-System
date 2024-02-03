package com.school.sba.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.school.sba.requestdto.ExcelRequestDto;
import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.service.ClassHourService;
import com.school.sba.utility.ResponseStructure;

@RestController
public class ClassHourController {
	@Autowired
	private ClassHourService classHourService;
	
	@PostMapping("/academic-program/{programId}/class-hours")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<ResponseStructure<ClassHourResponse>> addClassHour(@PathVariable int programId ,@RequestBody ClassHourRequest classHourRequest){
		return classHourService.addClassHour(programId,classHourRequest);
	}
	@PutMapping("/class-hours")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<ResponseStructure<List<ClassHourResponse>>> updateClassHour(@RequestBody List<ClassHourRequest> classHourRequests){
		return classHourService.updateClassHour(classHourRequests);
	}
	
    @PostMapping("/academic-program/{programId}/class-hours/write-excel")
    public ResponseEntity<ResponseStructure<String>> addClassHourUsingExcel(@PathVariable int programId,@RequestBody ExcelRequestDto excelRequestDto){
    	return classHourService.addClassHourUsingExcel(programId,excelRequestDto);
    } 
    @PostMapping("/academic-program/{programId}/class-hours/from/{fromDate}/to/{toDate}/write-excel")
    public ResponseEntity<ResponseStructure<String>>   writeToExcelSheet(@PathVariable int programId,@PathVariable LocalDate fromDate,@PathVariable LocalDate toDate, @RequestParam MultipartFile file){
    	return classHourService.addClassHourUsingMultipartFile(programId,fromDate,toDate,file);
    }
	
}