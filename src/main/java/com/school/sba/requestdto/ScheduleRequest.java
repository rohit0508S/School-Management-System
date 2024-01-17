package com.school.sba.requestdto;

import java.time.Duration;
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
	private int  classHoursPerDay;
	private Duration classHourLengthMinutes;
	private LocalTime breakTime;
	private Duration breakLengthMinutes;
	private LocalTime lunchTime;
	private Duration lunchLengthMinutes;
}