package com.school.sba.repositary;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.school.sba.entity.ClassHour;

public interface ClassHourRepository extends JpaRepository<ClassHour, Integer>{
	boolean existsByBeginsAtBetweenAndRoomNo(LocalDateTime startsAt, LocalDateTime endsAt, int roomNo);	
	List<ClassHour> findByIsUpdated(boolean isUpdated);
}
