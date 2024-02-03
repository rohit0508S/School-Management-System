package com.school.sba.service;


import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.school.sba.requestdto.ExcelRequestDto;
import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.utility.ResponseStructure;

public interface ClassHourService {

	
	ResponseEntity<ResponseStructure<ClassHourResponse>> addClassHour(int programId, ClassHourRequest classHourRequest);

	ResponseEntity<ResponseStructure<List<ClassHourResponse>>> updateClassHour(List<ClassHourRequest> classHourUpdateRequests);

	ResponseEntity<ResponseStructure<String>> addClassHourUsingExcel(int programId, ExcelRequestDto excelRequestDto);

	ResponseEntity<?> addClassHourUsingMultipartFile(int programId, LocalDate fromDate,
			LocalDate toDate, MultipartFile file) throws Exception;

}
