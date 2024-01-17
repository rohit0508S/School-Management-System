package com.school.sba.repositary;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.Users;
import com.school.sba.enums.UserRole;

public interface UserRepository extends JpaRepository<Users, Integer> {



	boolean existsByRole(UserRole role);

//	boolean existsByRole(UserRole admin);

//	boolean existsByRole(UserRole userRole);
Users findByUsername(String username);
    
    void deleteById(int userId);
	 
}
