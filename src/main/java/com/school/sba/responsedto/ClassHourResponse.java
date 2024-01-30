package com.school.sba.responsedto;

import java.time.LocalDateTime;
import com.school.sba.enums.CLASSSTATUS;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Builder
public class ClassHourResponse {
	private int classHourId;
	private LocalDateTime beginsAt ;
	private LocalDateTime endsAt;
	private int roomNo;
	private CLASSSTATUS classStatus;
}
