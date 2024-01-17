package com.school.sba.entity;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Schedule {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int scheduleId;
	private LocalTime opensAt;
	private LocalTime closesAt;
	private int  classHoursPerDay;
	private Duration classHourLengthMinutes;
	private LocalTime breakTime;
	private Duration breakLengthMinutes;
	private LocalTime lunchTime;
	private Duration lunchLengthMinutes;
	@OneToOne
    private School school;
}
