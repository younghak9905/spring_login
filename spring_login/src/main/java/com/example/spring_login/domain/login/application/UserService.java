package com.example.spring_login.domain.login.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.spring_login.entity.User;
import com.example.spring_login.exception.NotFoundUserException;
import com.example.spring_login.login.dto.request.SignUpDto;
import com.example.spring_login.oauth.utils.JwtUtil;
import com.example.spring_login.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;


	public User findByUserId(Long userId) {
		return userRepository.findByUserId(userId)
			.orElseThrow(NotFoundUserException::new);
	}




}