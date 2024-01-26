package com.school.sba.entity;

import java.util.List;
import com.school.sba.enums.USERROLE;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;
	@Column(unique = true)// user name should be unique
	private String userName;
	private String password;
	private String firstName;
	private String lastName;
	private long contactNo;
	@Column(unique = true)
	private String email;	
	@Enumerated(EnumType.STRING)
	private USERROLE userRole;	
	private boolean isDeleted;
	@ManyToOne
	private School school;	
	@ManyToMany(mappedBy = "users")
	private List<AcademicProgram> academicPrograms;	
	@ManyToOne
	private Subject subject;
		

}
