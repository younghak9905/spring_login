package com.example.spring_login.domain.login.api;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.example.spring_login.login.dto.response.JwtTokenResponseDto;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_login.login.application.AuthService;
import com.example.spring_login.login.application.KakaoLoginService;
import com.example.spring_login.login.application.UserService;
import com.example.spring_login.login.domain.enums.Provider;
import com.example.spring_login.login.dto.request.LoginDto;
import com.example.spring_login.login.dto.request.LoginRequestDto;
import com.example.spring_login.login.dto.request.SignUpDto;
import com.example.spring_login.login.dto.response.JwtTokenResponse;
import com.example.spring_login.oauth.dto.KakaoTokenResponse;
import com.example.spring_login.oauth.security.info.UserAuthentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "카카오 로그인 api / 담당자 : 이영학")
public class AuthController {

	private final AuthService authService;
	private final KakaoLoginService kakaoLoginService;

	private final UserService userService;

	private final PasswordEncoder passwordEncoder;

	private static final Logger logger = Logger.getLogger(AuthController.class.getName());


	@Value("${kakao.api.key}")
	private String apiKey;
	@Value("${kakao.redirect.url}")
	private String redirectUri;// Replace with your actual redirect URI
	@Operation(summary = "카카오 로그인 code 가져오기")
	@GetMapping("")
	public ResponseEntity<?> redirectToKakaoLogin(HttpServletResponse response) throws IOException {
		String clientId = apiKey;  // Replace with your Kakao REST API Key

		String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize"
			+ "?response_type=code"
			+ "&client_id=" + clientId
			+ "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.toString())
			+ "&scope=profile_nickname";

		// Set CORS headers
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
		response.setHeader("Access-Control-Allow-Credentials", "true");

		// Perform the redirect
		Map<String, String> responseBody = new HashMap<>();
		responseBody.put("redirectUrl", kakaoAuthUrl);
		return ResponseEntity.ok(responseBody);
	}
	@Operation(summary = "로그인 -> 아이디 로그인")
	@PostMapping("/login")
	public ResponseEntity<?> login(
		@Valid @RequestBody LoginDto dto
	)
	{
		String id = dto.getUsername();
		String pw = dto.getPw();
		// 유효성 검사
		if (!isValidId(id) || !isValidPassword(pw)) {
			return ResponseEntity.badRequest().body("Invalid ID or Password format");
		}

		// 로그인 처리
		return ResponseEntity.ok(authService.notSocialLogin(id, pw));
	}
	@Operation(summary = "회원가입")
	@PostMapping("/signUp")
	public ResponseEntity<?> signUp(
		@Valid @RequestBody SignUpDto dto
	){
		String id = dto.getUsername();
		String pw = dto.getPw();

		if (!isValidId(id) || !isValidPassword(pw)) {
			return ResponseEntity.badRequest().body("Invalid ID or Password format");
		}
		authService.createUser(dto);
		return ResponseEntity.ok("회원가입 되었습니다.");
	}

	private boolean isValidId(String id) {
		// ID 유효성 검사 로직 (예: 영숫자만 허용, 5~20자 사이)
		String idPattern = "^[a-zA-Z0-9]{5,20}$";
		return Pattern.matches(idPattern, id);
	}

	private boolean isValidPassword(String pw) {
		// 비밀번호 유효성 검사 로직 (예: 영숫자와 특수문자만 허용, 8~30자 사이)
		String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,30}$";
		return Pattern.matches(passwordPattern, pw);
	}



	@Operation(summary = "REST API 카카오 로그인")
	@GetMapping("/callback")
	public ResponseModel<?> kakaoCallback(@RequestParam String code) throws IOException {

		KakaoTokenResponse accessToken = kakaoLoginService.getAccessToken(code, apiKey, redirectUri);
		LoginRequest request = new LoginRequest(Provider.KAKAO, null);
		KakaoLoginRequest kakaoLoginRequestDto = KakaoLoginRequest.builder()
				.accessToken(accessToken.getAccess_token())
				.refreshToken(accessToken.getRefresh_token())
				.build();

		JwtTokenResponse tokens = authService.login(kakaoLoginRequestDto, request);
		return ResponseModel.success(tokens);
	}



	@Operation(summary = "카카오 로그아웃 하기")
	@PostMapping("/logout")
	public ResponseEntity<?> logout() {
		UserAuthentication authentication = (UserAuthentication)SecurityContextHolder.getContext().getAuthentication();
		authService.logout(authentication);
		return ResponseEntity.ok("로그아웃에 성공하였습니다.");
	}
	@Operation(summary = "카카오 로그인 리프레시 토큰 받기")
	@PostMapping("/refresh-kakao-token")
	public ResponseEntity<?> refreshKakaoToken(@RequestParam String refreshToken) throws IOException {
		KakaoTokenResponse response = kakaoLoginService.refreshKakaoToken(refreshToken);
		LoginRequestDto request = new LoginRequestDto(Provider.KAKAO, null);
		JwtTokenResponseDto tokens = authService.login(response, request);
		return ResponseEntity.ok((tokens));
	}

}
