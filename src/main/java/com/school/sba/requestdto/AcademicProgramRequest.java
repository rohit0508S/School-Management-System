package com.school.sba.requestdto;

import java.time.LocalDate;
import java.util.List;

import com.school.sba.enums.PROGRAMTYPE;
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
public class AcademicProgramRequest {
	
	private PROGRAMTYPE programtype;
	private String programName;
	private LocalDate beginsAt;
	private LocalDate endsAt;

}
