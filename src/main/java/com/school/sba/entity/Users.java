package com.school.sba.entity;

import java.util.List;

import com.school.sba.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
@Entity
public class Users {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;
	@Column(unique = true)
	private String username;
	@Column(nullable = false)
    private String password;
	private String firstName;
	private String lastName;
	private Long contactNo;
	@Column(unique = true)
	private String email;
	private UserRole role;
	private Boolean isDeleted = false; 
	@ManyToOne
    private School school;	
	@ManyToMany
	
	private List<AcademicProgram> academicProgram;
}
