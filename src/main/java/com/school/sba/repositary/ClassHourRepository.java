package com.school.sba.repositary;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.ClassHour;

public interface ClassHourRepository extends JpaRepository<ClassHour, Integer>{
	boolean existsByBeginsAtBetweenAndRoomNo(LocalDateTime startsAt, LocalDateTime endsAt, int roomNo);	
	List<ClassHour> findByIsUpdated(boolean isUpdated);
	
	@Query("Select ch from ClassHour ch where ch.academicProgram=:academicProgram "+"order by ch.classHourId DESC "+
	"LIMIT:lastNrecords")
	List<ClassHour> findLastNRecordsByAcademicProgram(AcademicProgram academicProgram,int lastNrecords);
	
	List<ClassHour> findAllByAcademicProgramAndBeginsAtBetween(AcademicProgram program,LocalDateTime from ,LocalDateTime to);
	
}
