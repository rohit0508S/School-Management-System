package com.school.sba.serviceimpl;

import java.time.Duration;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.school.sba.entity.Schedule;
import com.school.sba.exception.SchoolNotFoundByIdException;
import com.school.sba.repositary.ScheduleRepository;
import com.school.sba.repositary.SchoolRepositary;
import com.school.sba.requestdto.ScheduleRequest;
import com.school.sba.responsedto.ScheduleResponseDto;
import com.school.sba.responsedto.SchoolResponseDto;
import com.school.sba.service.ScheduleService;
import com.school.sba.utility.ResponseStructure;

public class ScheduleServiceImpl implements ScheduleService {
	
	@Autowired
	private SchoolRepositary schoolRepository;
	@Autowired
	private ScheduleRepository scheduleRepository;
	
	ResponseStructure<ScheduleResponseDto> responseStructure;
	
	
	private Schedule mapToSchedule(ScheduleRequest scheduleRequest) {
		return Schedule.builder()
				.opensAt(scheduleRequest.getOpensAt())
				.lunchTime(scheduleRequest.getLunchTime())
				.lunchLengthMinutes(scheduleRequest.getLunchLengthMinutes())
				.breakLengthMinutes(scheduleRequest.getBreakLengthMinutes())
				.breakTime(scheduleRequest.getBreakTime())
				.classHourLengthMinutes(scheduleRequest.getClassHourLengthMinutes())
				.classHoursPerDay(scheduleRequest.getClassHoursPerDay())
				.closesAt(scheduleRequest.getClosesAt()).build();
	}
	
	private ScheduleResponseDto mapToScheduleResponse(Schedule schedule) {
		return ScheduleResponseDto.builder()
				.opensAt(schedule.getOpensAt())
				.lunchTime(schedule.getLunchTime())
				.lunchLengthMinutes(schedule.getLunchLengthMinutes())
				.breakLengthMinutes(schedule.getBreakLengthMinutes())
				.breakTime(schedule.getBreakTime())
				.classHourLengthMinutes(schedule.getClassHourLengthMinutes())
				.classHoursPerDay(schedule.getClassHoursPerDay())
				.closesAt(schedule.getClosesAt()).build();
				
	}
	public ResponseEntity<ResponseStructure<ScheduleResponseDto>> createSchedule(ScheduleRequest scheduleRequest, int schoolId) {
	    ResponseStructure<ScheduleResponseDto> responseStructure = new ResponseStructure<>(); // Initialize responseStructure

	    return schoolRepository.findById(schoolId).map(school -> {
	        if (school.getSchedule() == null) {
	            Schedule schedule = mapToSchedule(scheduleRequest);
	            schedule = scheduleRepository.save(schedule);
	            school.setSchedule(schedule);
	            schoolRepository.save(school);

	            ScheduleResponseDto response = mapToScheduleResponse(schedule);
	            responseStructure.setStatus(HttpStatus.CREATED.value());
	            responseStructure.setMessage("Schedule created for the School");
	            responseStructure.setData(response);
	            return new ResponseEntity<>(responseStructure, HttpStatus.CREATED);
	        } else {
	            responseStructure.setStatus(HttpStatus.BAD_REQUEST.value());
	            responseStructure.setMessage("Schedule already exists for the School");
	            return new ResponseEntity<>(responseStructure, HttpStatus.BAD_REQUEST);
	        }
	    }).orElseThrow(() -> new SchoolNotFoundByIdException("School Id not present in the database"));
	}

	
	
}
