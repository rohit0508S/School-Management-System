package com.school.sba.service;

import org.springframework.http.ResponseEntity;
import com.school.sba.requestdto.ScheduleRequest;
import com.school.sba.responsedto.SchoolResponseDto;
import com.school.sba.utility.ResponseStructure;

public interface ScheduleService {	

	ResponseEntity<ResponseStructure<SchoolResponseDto>> createSchedule(ScheduleRequest scheduleRequest, int schoolId);

}
