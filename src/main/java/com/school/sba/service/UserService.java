package com.school.sba.service;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.utility.ResponseStructure;

public interface UserService {

	ResponseEntity<ResponseStructure<UserResponse>> register(UserRequest userRequest);

	ResponseEntity<ResponseStructure<UserResponse>> deleteUserById(int userId);

	ResponseEntity<ResponseStructure<UserResponse>> findUserById(int userId);

}
