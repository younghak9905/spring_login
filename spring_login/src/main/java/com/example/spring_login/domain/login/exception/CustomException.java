package com.example.spring_login.login.exception;

import com.example.spring_login.exception.ExceptionCode;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
	private final ExceptionCode code;

	public CustomException(ExceptionCode code) {
		super(code.getMessage());
		this.code = code;
	}
}