package com.example.spring_login.login.dto.request;



import com.example.spring_login.login.domain.enums.Provider;

import jakarta.validation.constraints.NotNull;

public record LoginRequestDto(@NotNull Provider provider, String name) {
}