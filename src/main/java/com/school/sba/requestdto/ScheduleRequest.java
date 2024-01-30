package com.school.sba.requestdto;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRequest {
	private LocalTime opensAt;
	private LocalTime closesAt;
	private int classHoursPerday;
	private int classHoursLengthInMinutes;
	private LocalTime breaktime;
	private int breakeLengthInMinutes;
	private LocalTime lunchTime;
	private int lunchBreakLengthInMinutes;
}
