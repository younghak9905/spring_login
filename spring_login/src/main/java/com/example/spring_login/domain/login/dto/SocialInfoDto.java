package com.example.spring_login.login.dto;

public record SocialInfoDto(String serialId, String email, String name) {
	public static SocialInfoDto of(String serialId, String email, String name) {
		return new SocialInfoDto(serialId, email, name);
	}
}