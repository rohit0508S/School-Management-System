package com.school.sba.serviceimpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Schedule;
import com.school.sba.entity.School;
import com.school.sba.entity.Subject;
import com.school.sba.entity.User;
import com.school.sba.enums.CLASSSTATUS;
import com.school.sba.enums.USERROLE;
import com.school.sba.exception.DataAlreadyExistException;
import com.school.sba.exception.ProgramNotFoundByIdException;
import com.school.sba.exception.ScheduleNotFoundException;
import com.school.sba.exception.UserNotFoundByIdException;
import com.school.sba.repositary.AcademicProgramRepositary;
import com.school.sba.repositary.ClassHourRepository;
import com.school.sba.repositary.SubjectRepositary;
import com.school.sba.repositary.UserRepositary;
import com.school.sba.requestdto.ClassHourUpdateRequest;
import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.service.ClassHourService;
import com.school.sba.utility.ResponseEntityProxy;
import com.school.sba.utility.ResponseStructure;

@Service
public class ClassHourImpl implements ClassHourService {
	@Autowired
	private AcademicProgramRepositary programRepo;
	@Autowired
	private ClassHourRepository classHourRepository;
	@Autowired
	private SubjectRepositary subjectRepositary;
	@Autowired
	private UserRepositary userRepositary;
	@Autowired
	private ResponseStructure<ClassHourResponse> structure;

	private ClassHourResponse mapToResponse(ClassHour classHour) {
		return ClassHourResponse.builder().classHourId(classHour.getClassHourId()).beginsAt(classHour.getBeginsAt())
				.endsAt(classHour.getEndsAt()).roomNo(classHour.getRoomNo()).build();
	}

	private LocalDateTime dateToDateTime(LocalDate date, LocalTime time) {
		return LocalDateTime.of(date, time);
	}

	private boolean isBreakTime(LocalDateTime currentTime, Schedule schedule) {
		LocalTime breakTimeStart = schedule.getBreakTime();
		LocalTime breakTimeEnd = breakTimeStart.plusMinutes(schedule.getBreakeLengthInMinutes().toMinutes());

		return (currentTime.toLocalTime().isAfter(breakTimeStart) && currentTime.toLocalTime().isBefore(breakTimeEnd));

	}

	private boolean isLunchTime(LocalDateTime currentTime, Schedule schedule) {
		LocalTime lunchTimeStart = schedule.getLunchTime();
		LocalTime lunchTimeEnd = lunchTimeStart.plusMinutes(schedule.getLunchBreakLengthInMinutes().toMinutes());

		return (currentTime.toLocalTime().isAfter(lunchTimeStart) && currentTime.toLocalTime().isBefore(lunchTimeEnd));

	}

	public ResponseEntity<ResponseStructure<ClassHourResponse>> addClassHour(int programId, ClassHourRequest request) {
		return programRepo.findById(programId).map(academicProgarm -> {
			School school = academicProgarm.getSchool();
			Schedule schedule = school.getSchedule();
			if (schedule == null) {
				throw new ScheduleNotFoundException("Failed to Generate class hour");
			}
			if (academicProgarm.getClassHour() == null || academicProgarm.getClassHour().isEmpty()) {
				List<ClassHour> perDayClassHours = new ArrayList<ClassHour>();
				LocalDate date = academicProgarm.getBeginsAt();

				for (int day = 1; day <= 6; day++) {
					LocalTime currentTime = schedule.getOpensAt();
					LocalDateTime lastHour = null;

					for (int entry = 1; entry <= schedule.getClassHoursPerday(); entry++) {
						ClassHour classHour = new ClassHour();
						if (currentTime.equals(schedule.getOpensAt())) {// first class hour of the day
							classHour.setBeginsAt(dateToDateTime(date, currentTime));
						} else if (currentTime.equals(schedule.getBreakTime())) {// after break time
							lastHour = lastHour.plus(schedule.getBreakeLengthInMinutes());
							classHour.setBeginsAt(dateToDateTime(date, lastHour.toLocalTime()));

						} else if (currentTime.equals(schedule.getLunchTime())) {// after lunch time
							lastHour = lastHour.plus(schedule.getLunchBreakLengthInMinutes());
							classHour.setBeginsAt(dateToDateTime(date, lastHour.toLocalTime()));
						} else {// rest class hours of that day
							classHour.setBeginsAt(dateToDateTime(date, lastHour.toLocalTime()));
						}
						classHour.setEndsAt(classHour.getBeginsAt().plus(schedule.getClassHoursLengthInMinutes()));
						classHour.setClassStatus(CLASSSTATUS.NOT_SCHEDULED);
						classHour.setAcademicProgram(academicProgarm);
						perDayClassHours.add(classHourRepository.save(classHour));
						lastHour = perDayClassHours.get(entry - 1).getEndsAt();
						currentTime = lastHour.toLocalTime();

						if (currentTime.equals(schedule.getClosesAt()))// school closing time
							break;
					}
					date = date.plusDays(1);
				}
				academicProgarm.setClassHour(perDayClassHours);
				programRepo.save(academicProgarm);

				structure.setStatus(programId);
				structure.setData(null);
				structure.setMessage("ClassHour Generated for::" + academicProgarm.getProgramName());

				return new ResponseEntity<ResponseStructure<ClassHourResponse>>(structure, HttpStatus.CREATED);
			} else
				throw new IllegalArgumentException("ClassHours Already Generated for::"
						+ academicProgarm.getProgramName() + "of Id:" + academicProgarm.getProgramId());
		}).orElseThrow(() -> new ProgramNotFoundByIdException("Invalid Program Id"));
	}

	@Override
	public ResponseEntity<ResponseStructure<List<ClassHourResponse>>>   updateClassHour(List<ClassHourRequest> classhourrequestlist) {
		
		List<ClassHourResponse> updatedClassHourResponses=new ArrayList<>();
		ResponseStructure<List<ClassHourResponse>> structure=new ResponseStructure<>();


		for(ClassHourRequest req : classhourrequestlist)
		{
			return	userRepositary.findById(req.getUserId()).map(user ->{

				return classHourRepository.findById(req.getClassHourId()).map(classHour->{

					return subjectRepositary.findById(req.getSubjectId()).map(subject->{

						if(user.getUserRole().equals(USERROLE.TEACHER) && user.getSubject().equals(subject))
						{

							boolean isPresent=classHourRepository.existsByBeginsAtBetweenAndRoomNo(classHour.getBeginsAt(), classHour.getEndsAt(), req.getRoomNo());

							if(isPresent)
							{
								throw new DataAlreadyExistException("class room already assigned");
							}

							else 
							{
								classHour.setSubject(subject);
								classHour.setUser(user);
								classHour.setRoomNo(req.getRoomNo());
								
								classHourRepository.save(classHour);

								updatedClassHourResponses.add(mapToResponse(classHour));
								
								
								structure.setStatus(HttpStatus.OK.value());
								structure.setMessage("Updated");
								structure.setData(updatedClassHourResponses);

								return new ResponseEntity<ResponseStructure<List<ClassHourResponse>>>(structure,HttpStatus.OK);
							}
						}

						else 
							throw new IllegalArgumentException("Unable to update teacher with given subject");


					}).orElseThrow(()->new IllegalArgumentException("subject not found "));

				}).orElseThrow(()->new IllegalArgumentException("Class houn not present"));

			}).orElseThrow(()->new UserNotFoundByIdException("user not found"));
		}
		return null;

	}
}
