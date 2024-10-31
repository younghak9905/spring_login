package com.example.spring_login.login.interceptor;

import java.security.Principal;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.example.spring_login.exception.EmptyPrincipalException;
import com.example.spring_login.login.application.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

	private final UserService userService;

	public UserArgumentResolver(UserService userService) {
		this.userService = userService;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().equals(com.example.spring_login.entity.User.class)
			&& parameter.hasParameterAnnotation(UserId.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter,
		ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory) {
		final Principal principal = webRequest.getUserPrincipal();
		if (principal == null) {
			throw new EmptyPrincipalException();
		}
		Long userId = Long.valueOf(principal.getName());
		return userService.findByUserId(userId);
	}
}
