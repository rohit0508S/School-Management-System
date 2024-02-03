package com.school.sba.serviceimpl;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Schedule;
import com.school.sba.entity.School;
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
import com.school.sba.requestdto.ExcelRequestDto;
import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.service.ClassHourService;
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

	private ClassHour mapToNewClassHour(ClassHour existingClassHour) {
		return ClassHour.builder().user(existingClassHour.getUser())
				.academicProgram(existingClassHour.getAcademicProgram()).roomNo(existingClassHour.getRoomNo())
				.beginsAt(existingClassHour.getBeginsAt().plusDays(7)).endsAt(existingClassHour.getEndsAt().plusDays(7))
				.classStatus(existingClassHour.getClassStatus()).subject(existingClassHour.getSubject()).build();
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
				int end = 6;
				DayOfWeek dayOfWeek = date.getDayOfWeek();
				if (!dayOfWeek.equals(DayOfWeek.MONDAY))
					end = end + (7 - dayOfWeek.getValue());

				for (int day = 1; day <= end; day++) {
					if (date.getDayOfWeek().equals(DayOfWeek.SUNDAY))
						date = date.plusDays(1);
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
						} else { // rest class hours of that day
							classHour.setBeginsAt(dateToDateTime(date, lastHour.toLocalTime()));
						}
						classHour.setEndsAt(classHour.getBeginsAt().plus(schedule.getClassHoursLengthInMinutes()));
						classHour.setClassStatus(CLASSSTATUS.NOT_SCHEDULED);
						classHour.setAcademicProgram(academicProgarm);
						classHour.setUpdated(true);
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
			}

			else
				throw new IllegalArgumentException("ClassHours Already Generated for::"
						+ academicProgarm.getProgramName() + "of Id:" + academicProgarm.getProgramId());
		}).orElseThrow(() -> new ProgramNotFoundByIdException("Invalid Program Id"));
	}

	@Override
	public ResponseEntity<ResponseStructure<List<ClassHourResponse>>> updateClassHour(
			List<ClassHourRequest> classhourrequestlist) {

		List<ClassHourResponse> updatedClassHourResponses = new ArrayList<>();
		ResponseStructure<List<ClassHourResponse>> structure = new ResponseStructure<>();
		for (ClassHourRequest req : classhourrequestlist) {
			return userRepositary.findById(req.getUserId()).map(user -> {
				return classHourRepository.findById(req.getClassHourId()).map(classHour -> {
					return subjectRepositary.findById(req.getSubjectId()).map(subject -> {
						if (user.getUserRole().equals(USERROLE.TEACHER) && user.getSubject().equals(subject)) {
							boolean isPresent = classHourRepository.existsByBeginsAtBetweenAndRoomNo(
									classHour.getBeginsAt(), classHour.getEndsAt(), req.getRoomNo());

							if (isPresent) {
								throw new DataAlreadyExistException("class room already assigned");
							}

							else {
								classHour.setSubject(subject);
								classHour.setUser(user);
								classHour.setRoomNo(req.getRoomNo());
								classHourRepository.save(classHour);
								updatedClassHourResponses.add(mapToResponse(classHour));
								structure.setStatus(HttpStatus.OK.value());
								structure.setMessage("Updated");
								structure.setData(updatedClassHourResponses);
								return new ResponseEntity<ResponseStructure<List<ClassHourResponse>>>(structure,
										HttpStatus.OK);
							}
						}

						else
							throw new IllegalArgumentException("Unable to update teacher with given subject");

					}).orElseThrow(() -> new IllegalArgumentException("subject not found "));

				}).orElseThrow(() -> new IllegalArgumentException("Class houn not present"));

			}).orElseThrow(() -> new UserNotFoundByIdException("user not found"));
		}
		return null;
	}

	public void autoGenerateClassHour() {
		List<ClassHour> classHour = classHourRepository.findByIsUpdated(true);
		for (ClassHour classHour1 : classHour) {
			classHourRepository.save(classHour1);
		}

	}

	public void generateWeeklyClassHours() {
		List<AcademicProgram> programToBeAutoRepeated = programRepo.findByAutoRepeatScheduledTrue();
		if (!programToBeAutoRepeated.isEmpty()) {
			programToBeAutoRepeated.forEach(program -> {
				int n = program.getSchool().getSchedule().getClassHoursPerday() * 6;
				// getting last week class hour
				List<ClassHour> lastWeekClassHours = classHourRepository.findLastNRecordsByAcademicProgram(program, n);
				if (!lastWeekClassHours.isEmpty()) {
					for (int i = lastWeekClassHours.size() - 1; i >= 0; i--) {
						ClassHour existClassHour = lastWeekClassHours.get(i);
						classHourRepository.save(mapToNewClassHour(existClassHour));
					}
					System.out.println("This week data generated as per last week data");
				}
				System.out.println("No last week data present");
			});
			System.out.println("Schedule successfully auto repeated for the upcoming week ");
		} else
			System.out.println("Auto repeat schedule: OFF");
	}

	
	
	
	
	
	@Override
	public ResponseEntity<ResponseStructure<String>> addClassHourUsingExcel(int programId,
			ExcelRequestDto excelRequestDto)   {
		
		
		
		Optional<AcademicProgram> optional = programRepo.findById(programId);
		AcademicProgram program = optional.get();
		String path="C:\\Users\\rohit kumar\\Desktop\\DemoSpringBootXl";
		String filePath=path+"demo.xlsx";
		
		
		LocalDateTime from=excelRequestDto.getFromDate().atTime(LocalTime.MIDNIGHT);
		LocalDateTime to=excelRequestDto.getToDate().atTime(LocalTime.MIDNIGHT).plusDays(1);
		List<ClassHour> classHours=classHourRepository.findAllByAcademicProgramAndBeginsAtBetween(program, from, to);
		
		
		XSSFWorkbook workbook=new XSSFWorkbook();
		XSSFSheet sheet=workbook.createSheet();
		
		int rowNumber=0;
		Row header=sheet.createRow(rowNumber);
		header.createCell(0).setCellValue("");
		header.createCell(1).setCellValue("BeginsAt");
		header.createCell(2).setCellValue("En");
		header.createCell(3).setCellValue("Subject");
		header.createCell(4).setCellValue("UserName");
		header.createCell(5).setCellValue("Room No");
		
		
		DateTimeFormatter timeFormatter=DateTimeFormatter.ofPattern("HH:mm");
		DateTimeFormatter dateFormatter=DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		for(ClassHour classHour:classHours) {
			Row row=sheet.createRow(++rowNumber);
			row.createCell(0).setCellValue(dateFormatter.format(classHour.getBeginsAt()));
			row.createCell(1).setCellValue(timeFormatter.format(classHour.getBeginsAt()));
			row.createCell(2).setCellValue(timeFormatter.format(classHour.getEndsAt()));
			if(classHour.getUser()==null)
				row.createCell(3).setCellValue(classHour.getUser().getUserName());
			else
				row.createCell(3).setCellValue(classHour.getUser().getUserName());
			   if(classHour.getSubject()==null) 
			row.createCell(4).setCellValue(classHour.getSubject().getSubjectName());
			   else
				   row.createCell(4).setCellValue(classHour.getSubject().getSubjectName());
			row.createCell(5).setCellValue(classHour.getRoomNo());
		}
		try {
			workbook.write(new FileOutputStream(filePath));
		} catch (FileNotFoundException e) {
			System.out.println("File is not found!!!!");
		} catch (IOException e) {			
			e.printStackTrace();
		}		
		structure.setMessage("Created excel sheet");
		structure.setStatus(HttpStatus.CREATED.value());
		structure.setData(null);
		
	return new ResponseEntity<ResponseStructure<String>>(HttpStatus.CREATED);
	}
	
	
	
	
	
	
	
	
	//file content excel sheet
	
	

	@Override
	public ResponseEntity<?> addClassHourUsingMultipartFile(int programId, LocalDate fromDate,
			LocalDate toDate, MultipartFile file) throws Exception  {
		Optional<AcademicProgram> optional = programRepo.findById(programId);
		AcademicProgram program = optional.get();
		
		LocalDateTime from=fromDate.atTime(LocalTime.MIDNIGHT);
		LocalDateTime to=toDate.atTime(LocalTime.MIDNIGHT).plusDays(1);
		List<ClassHour> classHours=classHourRepository.findAllByAcademicProgramAndBeginsAtBetween(program, from, to);
		
		

		DateTimeFormatter timeFormatter=DateTimeFormatter.ofPattern("HH:mm");
		DateTimeFormatter dateFormatter=DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		XSSFWorkbook workbook = null;
		
			workbook = new XSSFWorkbook(file.getInputStream());
		
		workbook.forEach(sheet->{
			int rowNumber=0;
			Row header=sheet.createRow(rowNumber);
			header.createCell(0).setCellValue("");
			header.createCell(1).setCellValue("BeginsAt");
			header.createCell(2).setCellValue("En");
			header.createCell(3).setCellValue("Subject");
			header.createCell(4).setCellValue("UserName");
			header.createCell(5).setCellValue("Room No");
			
			for(ClassHour classHour:classHours) {
				Row row=sheet.createRow(++rowNumber);
				row.createCell(0).setCellValue(dateFormatter.format(classHour.getBeginsAt()));
				row.createCell(1).setCellValue(timeFormatter.format(classHour.getBeginsAt()));
				row.createCell(2).setCellValue(timeFormatter.format(classHour.getEndsAt()));
				if(classHour.getUser()==null)
					row.createCell(3).setCellValue(classHour.getUser().getUserName());
				else
					row.createCell(3).setCellValue(classHour.getUser().getUserName());
				   if(classHour.getSubject()==null) 
				row.createCell(4).setCellValue(classHour.getSubject().getSubjectName());
				   else
					   row.createCell(4).setCellValue(classHour.getSubject().getSubjectName());
				row.createCell(5).setCellValue(classHour.getRoomNo());
			}
		});
		
	ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
	
		workbook.write(outputStream);
	
		workbook.close();
	
	   byte[] byteData=outputStream.toByteArray();
	   
	 
	   return ResponseEntity.ok()
			   .header("Content Disposition","attachment; filename="+file.getOriginalFilename())
			   .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
			   .body(byteData);
		
		return null;
	}
}
