package com.school.sba.serviceimpl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Schedule;
import com.school.sba.entity.School;
import com.school.sba.enums.CLASSSTATUS;
import com.school.sba.exception.ProgramNotFoundByIdException;
import com.school.sba.exception.ScheduleNotFoundException;
import com.school.sba.repositary.AcademicProgramRepositary;
import com.school.sba.repositary.ClassHourRepository;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.service.ClassHourService;
import com.school.sba.utility.ResponseEntityProxy;
import com.school.sba.utility.ResponseStructure;
@Service
public class ClassHourImpl implements ClassHourService{
	@Autowired
	AcademicProgramRepositary programRepo;
	@Autowired
	ClassHour classHour;
	@Autowired
	ClassHourRepository classHourRepository;
	
	private boolean isBreakTime(LocalDateTime currentTime , Schedule schedule)
	{
		LocalTime breakTimeStart = schedule.getBreakTime();
		LocalTime breakTimeEnd = breakTimeStart.plusMinutes(schedule.getBreakeLengthInMinutes().toMinutes());
		
		return (currentTime.toLocalTime().isAfter(breakTimeStart) && currentTime.toLocalTime().isBefore(breakTimeEnd));

	}
	
	private boolean isLunchTime(LocalDateTime currentTime , Schedule schedule)
	{
		LocalTime lunchTimeStart = schedule.getLunchTime();
		LocalTime lunchTimeEnd = lunchTimeStart.plusMinutes(schedule.getLunchBreakLengthInMinutes().toMinutes());
		
		return (currentTime.toLocalTime().isAfter(lunchTimeStart) && currentTime.toLocalTime().isBefore(lunchTimeEnd));

	}
	

	public ResponseEntity<ResponseStructure<String>> addClassHour(int programId) {
		return programRepo.findById(programId)
				.map(academicProgarm -> {
					School school = academicProgarm.getSchool();
					Schedule schedule = school.getSchedule();
					if(schedule!=null)
					{
						int classHourPerDay = schedule.getClassHoursPerday();
						int classHourLength = (int) schedule.getClassHoursLengthInMinutes().toMinutes();
						
						LocalDateTime currentTime = LocalDateTime.now().with(schedule.getOpensAt());
						
						LocalTime lunchTimeStart = schedule.getLunchTime();
						LocalTime lunchTimeEnd = lunchTimeStart.plusMinutes(schedule.getLunchBreakLengthInMinutes().toMinutes());
						LocalTime breakTimeStart = schedule.getBreakTime();
						LocalTime breakTimeEnd = breakTimeStart.plusMinutes(schedule.getBreakeLengthInMinutes().toMinutes());
						
						for(int day = 1 ; day<=6 ; day++)
						{
							for(int hour = 0;hour<classHourPerDay+2;hour++)
							{
								ClassHour classHour = new ClassHour();
								
								if(!currentTime.toLocalTime().equals(lunchTimeStart) && !isLunchTime(currentTime, schedule))
								{
									if(!currentTime.toLocalTime().equals(breakTimeStart) && !isBreakTime(currentTime, schedule))
									{
										LocalDateTime beginsAt = currentTime;
										LocalDateTime endsAt = beginsAt.plusMinutes(classHourLength);
										
										classHour.setBeginsAt(beginsAt);
										classHour.setEndsAt(endsAt);
										classHour.setClassStatus(CLASSSTATUS.NOT_SCHEDULED);
										
										currentTime = endsAt;
									}
									else
									{
										classHour.setBeginsAt(currentTime);
										classHour.setEndsAt(LocalDateTime.now().with(breakTimeEnd));
										classHour.setClassStatus(CLASSSTATUS.BREAK_TIME);
										currentTime = currentTime.plusMinutes(schedule.getBreakeLengthInMinutes().toMinutes());
									}
								}
								else
								{
									classHour.setBeginsAt(currentTime);
									classHour.setEndsAt(LocalDateTime.now().with(lunchTimeEnd));
									classHour.setClassStatus(CLASSSTATUS.LUNCH_TIME);
									currentTime = currentTime.plusMinutes(schedule.getLunchBreakLengthInMinutes().toMinutes());
								}
								classHour.setAcademicProgram(academicProgarm);
								classHourRepository.save(classHour);
							}
							currentTime = currentTime.plusDays(1).with(schedule.getOpensAt());
						}
			
					}
					else
						throw new ScheduleNotFoundException("The school does not contain any schedule, please provide a schedule to the school");
					
					return ResponseEntityProxy.getResponseEntity(HttpStatus.CREATED, "ClassHour generated successfully for the academic progarm","Class Hour generated for the current week successfully");
				})
				.orElseThrow(() -> new ProgramNotFoundByIdException("Invalid Program Id"));
			}

}
