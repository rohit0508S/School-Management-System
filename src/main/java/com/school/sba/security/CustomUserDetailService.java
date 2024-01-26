package com.school.sba.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.school.sba.entity.User;
import com.school.sba.exception.UserNotFoundBYUserNameException;
import com.school.sba.repositary.UserRepositary;
@Service
public class CustomUserDetailService implements UserDetailsService{
    @Autowired
	private UserRepositary userRepositary;
    private User user;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepositary.findByUserName(username).map((user)-> new CustomUserDetails(user)).orElseThrow(()->new UserNotFoundBYUserNameException("User not found "));
	}		

}