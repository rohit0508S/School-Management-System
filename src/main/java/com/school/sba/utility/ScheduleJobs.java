package com.school.sba.utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.school.sba.entity.ClassHour;
import com.school.sba.serviceimpl.AcademicProgramServiceImpl;
import com.school.sba.serviceimpl.ClassHourImpl;
import com.school.sba.serviceimpl.UserServiceImpl;

@Component
public class ScheduleJobs {	
	@Autowired
	UserServiceImpl userService;
	@Autowired
	AcademicProgramServiceImpl program;
	@Autowired
	ClassHourImpl classHour;
	
//	@Scheduled(fixedDelay = 1000*60)
	public void deleteUser() {
		userService.deleteSoftDeletedUser();
		System.out.println("::deleted data of user soft-deleted data");		
	}	
//	@Scheduled(fixedDelay = 1000*60)
	public void deleteAcademicProgram() {
		program.deleteAcademicProgramPermanently();
		System.out.println("::deleted data of academic-program data");
	}
//	@Scheduled(fixedDelay = 1000*60)
	public void updateClassHour() {
		classHour.autoGenerateClassHour();
		System.out.println("::Update class hour ");
	}
	
	
}
