package com.school.sba.responsedto;

import java.time.LocalTime;
import java.util.List;

import com.school.sba.entity.Subject;

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
public class AcademicProgramResponse {
	private String programName;
	private LocalTime beginsAt;
	private LocalTime endsAt;
	private List<String> subject;
	
}
