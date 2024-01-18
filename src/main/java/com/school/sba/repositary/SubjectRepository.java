package com.school.sba.repositary;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {
	 Subject findBySubjectName(String subjectName);
}
