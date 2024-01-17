package com.school.sba.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.school.sba.entity.Schedule;

import com.school.sba.repositary.ScheduleRepository;
import com.school.sba.repositary.SchoolRepositary;
import com.school.sba.requestdto.ScheduleRequest;
import com.school.sba.responsedto.ScheduleResponseDto;
import com.school.sba.responsedto.SchoolResponseDto;
import com.school.sba.service.ScheduleService;
import com.school.sba.utility.ResponseStructure;
@Service
public class ScheduleServiceImpl implements ScheduleService{
	@Autowired
	private SchoolRepositary schoolRepository;
	@Autowired
	private ScheduleRepository scheduleRepository;
	@Autowired
	ResponseStructure<SchoolResponseDto> responseStructure;
	
	private Schedule mapToSchedule(ScheduleRequest scheduleRequestDto) {
		return Schedule.builder().opensAt(scheduleRequestDto.getOpensAt())
				.closesAt(scheduleRequestDto.getClosesAt())
				.classHourLengthMinutes(scheduleRequestDto.getClassHourLengthMinutes())
				.classHourLengthMinutes(scheduleRequestDto.getClassHourLengthMinutes())
				.breakLengthMinutes(scheduleRequestDto.getBreakLengthMinutes())
				.lunchTime(scheduleRequestDto.getLunchTime())
				.lunchLengthMinutes(scheduleRequestDto.getLunchLengthMinutes()).build();
	}
	private Schedule mapToScheduleResponse(Schedule schedule) {
		
		return Schedule.builder()
				.scheduleId(schedule.getScheduleId())
				.opensAt(schedule.getOpensAt())
				.closesAt(schedule.getClosesAt())
				.classHourLengthMinutes(schedule.getClassHourLengthMinutes())
				.classHourLengthMinutes(schedule.getClassHourLengthMinutes())
				.breakLengthMinutes(schedule.getBreakLengthMinutes())
				.lunchTime(schedule.getLunchTime())
				.lunchLengthMinutes(schedule.getLunchLengthMinutes()).build();
	}
	public ResponseEntity<ResponseStructure<ScheduleResponseDto>> createSchedule(ScheduleRequest scheduleRequest,int schoolId) {
		return	schoolRepository.findById(schoolId).map(s->{
			if(s.getSchedule()==null) {
				Schedule schedule = mapToSchedule(scheduleRequest);
				schedule=scheduleRepository.save(schedule);
				s.setSchedule(schedule);
				schoolRepository.save(s);
				Schedule response = mapToScheduleResponse(schedule);
				responseStructure.setStatus(HttpStatus.CREATED.value());
				responseStructure.setMessage("Schedule created to School");
//				responseStructure.setData(response);
				return  new ResponseEntity<ResponseStructure<ScheduleResponseDto>>(HttpStatus.CREATED);
			} else {
				responseStructure.setStatus(HttpStatus.BAD_REQUEST.value());
				responseStructure.setMessage("Schedule created to School");
				return  new ResponseEntity<ResponseStructure<ScheduleResponseDto>>(responseStructure,HttpStatus.BAD_REQUEST);
				}
			}).orElseThrow(()->new SchoolNotFoundByIdException("School Id not present in the database"));
			
		
	}
	
	
//	public ResponseEntity<ResponseStructure<SchoolResponseDto>> createSchedule(ScheduleRequest scheduleRequest,int schoolId) {
//		return	(ResponseEntity<ResponseStructure<SchoolResponseDto>>) schoolRepository.findById(schoolId).map(s->{
//			if(s.getSchedule()==null) {
//				Schedule schedule = mapToSchedule(scheduleRequest);
//				schedule=scheduleRepository.save(schedule);
//				s.setSchedule(schedule);
//				schoolRepository.save(s);
//				Schedule response = mapToScheduleResponse(schedule);
//				responseStructure.setStatus(HttpStatus.CREATED.value());
//				responseStructure.setMessage("Schedule created to School");
////				responseStructure.setData(response);
////				responseStructure.setData(response);
//				return  new ResponseEntity<ResponseStructure<ScheduleRequest>>(HttpStatus.CREATED);
//			} else {
//				responseStructure.setStatus(HttpStatus.BAD_REQUEST.value());
//				responseStructure.setMessage("Schedule created to School");
//				return  new ResponseEntity<ResponseStructure<ScheduleResponse>>(HttpStatus.BAD_REQUEST);
//				}
//			}).orElseThrow(()->new SchoolNotFoundByIdException("School Id not present in the database"));
//			
//		
//	}
	
	
	
	
	
	

	

	

}


