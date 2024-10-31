package com.example.spring_login.login.exception;

import static com.example.spring_login.login.exception.LoginExceptionCode.*;

public class NotFoundUserInfoException extends CustomException {
	public NotFoundUserInfoException() {
		super(NOT_FOUND_USER_INFO);
	}
}
