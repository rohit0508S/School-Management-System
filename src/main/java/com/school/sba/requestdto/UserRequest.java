package com.school.sba.requestdto;

import com.school.sba.enums.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class UserRequest {
	@NotNull(message = "userName can not be Null")
	@Pattern(regexp = "^[A-Z][a-z](\\s[A-Z][a-z])?$", message = "Username should follow the initcap format")
	private String username;	
	private String firstName;
	private String lastName;
	@Pattern(regexp = "^(?=.*[0-9])")
	private Long contactNo;
	@NotBlank(message = "email can not be blank")
	@Email(regexp = "[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,}", message = "invalid email ")
	private String email;
	@NotEmpty(message = "Password Can not be Empty")
	@Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
	@Pattern(regexp = "^(?=.[0-9])(?=.[a-z])(?=.[A-Z])(?=.[@#$%^&+=])(?=\\S+$).{8,}$", message = "Password must" + " contain at least one letter, one number, one special character")
	private String password;
	private UserRole userRole;
}