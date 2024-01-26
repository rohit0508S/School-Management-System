package com.school.sba.requestdto;

import com.school.sba.entity.Schedule;

import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class SchoolRequest {
	
	private String schoolName;
	private long contactNo;
	private String emailId;
	private String address;
}

