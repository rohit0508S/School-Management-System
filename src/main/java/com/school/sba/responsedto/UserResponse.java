package com.school.sba.responsedto;

import com.school.sba.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
	private int userId;
	private String username;
	private String firstName;
	private String lastName;
	private Long contactNo;
	private String email;
	private UserRole userRole;
}
