package com.school.sba.serviceimpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import com.school.sba.entity.Users;
import com.school.sba.enums.UserRole;
import com.school.sba.repositary.UserRepository;
import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.UserService;
import com.school.sba.utility.ResponseStructure;
@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ResponseStructure<UserResponse> responseStructure;
	


	private Users mapToUser(UserRequest userRequest) {
		return Users.builder()				
				.username(userRequest.getUsername())
				.firstName(userRequest.getFirstName())
				.lastName(userRequest.getLastName())
				.email(userRequest.getEmail())
				.password(userRequest.getPassword())
				.contactNo(userRequest.getContactNo())
				.role(userRequest.getRole()).build();
	}
	private UserResponse mapToUSerResponse(Users user) {
		return UserResponse.builder()
				.userId(user.getUserId())
				.username(user.getUsername())
				.email(user.getEmail())
				.contactNo(user.getContactNo())
				.firstName(user.getFirstName())
				.lastName(user.getLastName())
				.role(user.getRole()).build();
	}


	
	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> register(UserRequest userRequest) {
			Users user=mapToUser(userRequest);
			user.setIsDeleted(false);
			 if (user.getRole() == UserRole.ADMIN) {			        
			        if (userRepository.existsByRole(UserRole.ADMIN)) {
			            responseStructure.setStatus(HttpStatus.BAD_REQUEST.value());
			            responseStructure.setMessage("An ADMIN user already exists!");
			            return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.BAD_REQUEST);
			        }
			    }
				user =userRepository.save(user);
				UserResponse response=mapToUSerResponse(user);
				responseStructure.setStatus(HttpStatus.CREATED.value());
				responseStructure.setMessage("User data Saved Successfully...!");
				responseStructure.setData(response);	
			return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure,HttpStatus.CREATED);
		}
	
	
	@Override
//	public ResponseEntity<ResponseStructure<UserResponse>> deleteUserById(int userId) {
	
//			Users user = userRepository.findById(userId).orElseThrow(() -> new com.school.sba.exception.UserNotFoundByIdException("Given userId->" + userId + " Not Found"));
//				
//			userRepository.delete(user);
//			UserResponse mapToUSerResponse = mapToUSerResponse(user);
//				
//			responseStructure.setStatus(HttpStatus.OK.value());
//			responseStructure.setMessage("Data Deleted Successfully...!");
//			responseStructure.setData(mapToUSerResponse);
//			return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.OK);		
//	}
	
	public ResponseEntity<ResponseStructure<UserResponse>> deleteUserById(int userId) {
        
            Optional<Users> optionalUser = userRepository.findById(userId);

            if (optionalUser.isPresent()) {
                Users user = optionalUser.get();
                user.setIsDeleted(true);
                userRepository.save(user);
                responseStructure.setStatus(HttpStatus.OK.value());
    			responseStructure.setMessage("Data Deleted Successfully...!");

                
                return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.OK);
            } else {
            	 responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
     			responseStructure.setMessage("User Not Found...!");
                return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.OK);
            }
        
    }
	
	
		
	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> findUserById(int userId) {
		Users user = userRepository.findById(userId).orElseThrow(() -> new com.school.sba.exception.UserNotFoundByIdException("Given userId->" + userId + " Not Found"));
		userRepository.findById(userId);
		UserResponse mapToUSerResponse = mapToUSerResponse(user);
		responseStructure.setStatus(HttpStatus.FOUND.value());
		responseStructure.setMessage("Data fetch Successfully...!");
		responseStructure.setData(mapToUSerResponse);
		return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.FOUND);
	}

}
