package com.example.spring_login.domain.login.api;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

import com.example.spring_login.SpringLoginApplication;

@Configuration
@EnableFeignClients(basePackageClasses = SpringLoginApplication.class)
public class FeignClientConfig {
}