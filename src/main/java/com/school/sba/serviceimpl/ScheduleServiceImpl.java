package com.school.sba.serviceimpl;

import java.time.Duration;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Schedule;
import com.school.sba.exception.SchoolNotFoundByIdException;
import com.school.sba.repositary.ScheduleRepository;
import com.school.sba.repositary.SchoolRepositary;
import com.school.sba.requestdto.ScheduleRequest;
import com.school.sba.responsedto.ScheduleResponseDto;
import com.school.sba.service.ScheduleService;
import com.school.sba.utility.ResponseStructure;
@Service
public class ScheduleServiceImpl implements ScheduleService {
	
	@Autowired
	private SchoolRepositary schoolRepository;
	@Autowired
	private ScheduleRepository scheduleRepository;
	@Autowired
	private ResponseStructure<ScheduleResponseDto> responseStructure;
	
	
	private Schedule mapToSchedule(ScheduleRequest scheduleRequest) {
	    return Schedule.builder()
	            .opensAt(scheduleRequest.getOpensAt())
	            .closesAt(scheduleRequest.getClosesAt())
	            .classHoursPerDay(scheduleRequest.getClassHoursPerDay())
	            .classHourLengthMinutes(Duration.ofMinutes(scheduleRequest.getClassHourLengthMinutes()))
	            .breakTime(scheduleRequest.getBreakTime())
	            .breakLengthMinutes(Duration.ofMinutes(scheduleRequest.getBreakLengthMinutes()))
	            .lunchTime(scheduleRequest.getLunchTime())
	            .lunchLengthMinutes(Duration.ofMinutes(scheduleRequest.getLunchLengthMinutes()))
	            .build();
	}

	private ScheduleResponseDto mapToScheduleResponse(Schedule schedule) {
	    return ScheduleResponseDto.builder()
	            .scheduleId(schedule.getScheduleId())
	            .opensAt(schedule.getOpensAt())
	            .closesAt(schedule.getClosesAt())
	            .classHoursPerDay(schedule.getClassHoursPerDay())
	            .classHourLengthMinutes(schedule.getClassHourLengthMinutes().toMinutesPart())
	            .breakTime(schedule.getBreakTime())
	            .breakLengthMinutes(schedule.getBreakLengthMinutes().toMinutesPart())
	            .lunchTime(schedule.getLunchTime())
	            .lunchLengthMinutes(schedule.getLunchLengthMinutes().toMinutesPart())
	            .build();
	}
	public ResponseEntity<ResponseStructure<ScheduleResponseDto>> createSchedule(ScheduleRequest scheduleRequest, int schoolId) {
	    ResponseStructure<ScheduleResponseDto> responseStructure = new ResponseStructure<>(); 

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

	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponseDto>> getSchedule(int schoolId) {
	    return schoolRepository.findById(schoolId).map(school -> {
	        Optional<Schedule> optional2 = scheduleRepository.findById(schoolId);
	        if (optional2.isPresent()) {
	            Schedule schedule = optional2.get();
	            ScheduleResponseDto response = mapToScheduleResponse(schedule);
	            responseStructure.setStatus(HttpStatus.FOUND.value());
	            responseStructure.setMessage("found the School Schedule");
	            responseStructure.setData(response);
	            return new ResponseEntity<ResponseStructure<ScheduleResponseDto>>(responseStructure, HttpStatus.FOUND);
	        } else {
	            responseStructure.setStatus(HttpStatus.BAD_REQUEST.value());
	            responseStructure.setMessage("for given schoolId there is no schedule registered in the database");
	            return new ResponseEntity<ResponseStructure<ScheduleResponseDto>>(responseStructure,HttpStatus.BAD_REQUEST);
	        }
	    }).orElseThrow(()-> new SchoolNotFoundByIdException("Given schoolId not in the database"));
	}

	
	public ResponseEntity<ResponseStructure<ScheduleResponseDto>> updateSchedule(int schoolId) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
}
