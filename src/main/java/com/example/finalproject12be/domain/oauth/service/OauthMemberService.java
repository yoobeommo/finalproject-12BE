package com.example.finalproject12be.domain.oauth.service;

import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.example.finalproject12be.domain.oauth.dto.KakaoMemberInfoRequest;
import com.example.finalproject12be.domain.oauth.entity.OauthMember;
import com.example.finalproject12be.domain.oauth.repository.OauthMemberRepository;
import com.example.finalproject12be.security.jwt.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OauthMemberService {

	private final OauthMemberRepository oauthMemberRepository;
	private final JwtUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;

	public String[] kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
		String[] tokenArray = getToken(code);
		// 1. "인가 코드"로 "액세스, refresh 토큰" 요청
		String accessToken = tokenArray[0];
		String refreshToken = tokenArray[1];

		// 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
		KakaoMemberInfoRequest kakaoMemberInfo = getKakaoUserInfo(accessToken);

		// 3. 필요시에 회원가입
		OauthMember kakaoMember = registerKakaoUserIfNeeded(kakaoMemberInfo);

		// 4. JWT 토큰 반환
		String createAccessToken =  jwtUtil.createToken(kakaoMember.getEmail(),"Access");
		String createRefreshToken =  jwtUtil.createToken(kakaoMember.getEmail(),"Refresh");

		//        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, createToken);
		String[] tokenArrayResult = new String[2];
		tokenArrayResult[0] = createAccessToken;
		tokenArrayResult[1] = createRefreshToken;

		return tokenArrayResult;
	}

	// 1. "인가 코드"로 "액세스 토큰" 요청
	private String[] getToken(String code) throws JsonProcessingException {

		// HTTP Header 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// HTTP Body 생성
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "authorization_code");
		body.add("client_id", "7463ed7e96bc168b9023480e535add90");
		body.add("redirect_uri", "http://localhost:3000/user/signin/kakao");
		body.add("code", code);

		// HTTP 요청 보내기
		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
			new HttpEntity<>(body, headers);
		RestTemplate rt = new RestTemplate();
		ResponseEntity<String> response = rt.exchange(
			"https://kauth.kakao.com/oauth/token",
			HttpMethod.POST,
			kakaoTokenRequest,
			String.class
		);

		// HTTP 응답 (JSON) -> 액세스 토큰 파싱
		String responseBody = response.getBody();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(responseBody);

		String[] tokenArray = new String[2];
		tokenArray[0] = jsonNode.get("access_token").asText();
		tokenArray[1] = jsonNode.get("refresh_token").asText();

		return tokenArray;
	}

	// 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
	private KakaoMemberInfoRequest getKakaoUserInfo(String accessToken) throws JsonProcessingException {

		// HTTP Header 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// HTTP 요청 보내기
		HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);

		RestTemplate rt = new RestTemplate();
		ResponseEntity<String> response = rt.exchange(
			"https://kapi.kakao.com/v2/user/me",
			HttpMethod.POST,
			kakaoUserInfoRequest,
			String.class
		);

		String responseBody = response.getBody();

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(responseBody);

		Long id = jsonNode.get("id").asLong();
		String nickname = jsonNode.get("properties")
			.get("nickname").asText();
		String email = jsonNode.get("kakao_account")
			.get("email").asText();

		log.info("카카오 사용자 정보: " + id + ", " + nickname + ", " + email);
		return new KakaoMemberInfoRequest(id,nickname, email);
	}

	// 3. 필요시에 회원가입
	private OauthMember registerKakaoUserIfNeeded(KakaoMemberInfoRequest kakaoUserInfo) {
		// DB 에 중복된 Kakao email 가 있는지 확인
		Long kakaoId = kakaoUserInfo.getId();
		OauthMember kakaoUser = oauthMemberRepository.findById(kakaoId)
			.orElse(null);
		if (kakaoUser == null) {
			// 신규 회원가입
			String email = kakaoUserInfo.getEmail();
			String nickname = kakaoUserInfo.getNickname();
			kakaoUser = new OauthMember(kakaoId, email, nickname);
			oauthMemberRepository.save(kakaoUser);
		}
		return kakaoUser;
	}
}
