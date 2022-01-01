package com.sparta.mbti.config.oauth2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.mbti.dto.KakaoUserInfoDto;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class KakaoOAuth2 {
    public KakaoUserInfoDto getKakaoUserInfo(String authorizedCode) throws JsonProcessingException {
            // 1. "인가 코드"로 "액세스 토큰" 요청
            String accessToken = getAccessToken(authorizedCode);

            return getUserInfo(accessToken);
    }

        @Value("${spring.datasource.client_id}")
        private String client_id;

        @Value("${spring.datasource.redirect_uri}")
        private String redirect_uri;


        // 1. "인가 코드"로 "액세스 토큰" 요청
        public String getAccessToken(String authorizedCode) throws JsonProcessingException{

                // HTTP Header 생성
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

                // HTTP Body 생성
                MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
                body.add("grant_type", "authorization_code");
                body.add("client_id", client_id);                  // 개발 REST API 키
                body.add("redirect_uri", redirect_uri);      // 개발 Redirect URI
                body.add("code", authorizedCode);

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
                // JSON -> Java Object
                String responseBody = response.getBody();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                return jsonNode.get("access_token").asText();
        }

        // 2. "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        private KakaoUserInfoDto getUserInfo(String accessToken) throws JsonProcessingException {
                // HTTP Header 생성
                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " + accessToken);      // JWT 토큰
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

                // HTTP 응답 (JSON) -> 액세스 토큰 파싱
                // JSON -> Java Object
                // 이 부분에서 카톡 프로필 정보 가져옴
                JSONObject body = new JSONObject(response.getBody());
                System.out.println(body);
                // ID (카카오 기본키)
                Long id = body.getLong("id");
                // 아이디 (이메일)
                String username = body.getJSONObject("kakao_account").getString("email");
                // 닉네임
                String nickname = body.getJSONObject("properties").getString("nickname");

                // profile_image_needs_agreement: true (이미지 동의 안함), false (이미지 동의)
                // is_default_image: true (기본 이미지), false (이미지 등록됨)
                // 프로필 이미지
                String profileImage = "";
                // 이미지 동의 및 등록 되었으면
                if (!body.getJSONObject("kakao_account").getBoolean("profile_image_needs_agreement") &&
                        !body.getJSONObject("kakao_account").getJSONObject("profile").getBoolean("is_default_image")) {
                        profileImage = body.getJSONObject("kakao_account").getJSONObject("profile").getString("profile_image_url");
                }

                // has_gender: false (성별 선택 안함), true (성별 선택)
                // gender_needs_agreement: true (성별 동의 안함), false (성별 동의)
                // 성별 (male, female, unchecked)
                String gender = "";
                // 성별 선택 및 성별 동의 되었으면
                if (body.getJSONObject("kakao_account").getBoolean("has_gender") &&
                        !body.getJSONObject("kakao_account").getBoolean("gender_needs_agreement")) {
                        gender = body.getJSONObject("kakao_account").getString("gender");
                }

                // has_age_range: false (연령대 선택 안함), true (연령대 선택)
                // age_range_needs_agreement: true (연령대 동의 안함), false (연령대 동의)
                // 연령대
                String ageRange = "";
                // 이미지 동의 및 등록 되었으면
                if (body.getJSONObject("kakao_account").getBoolean("has_age_range") &&
                        !body.getJSONObject("kakao_account").getBoolean("age_range_needs_agreement")) {
                        ageRange = body.getJSONObject("kakao_account").getString("age_range");
                }

                return KakaoUserInfoDto.builder()
                        .id(id)
                        .username(username)
                        .nickname(nickname)
                        .profileImage(profileImage)
                        .gender(gender)
                        .ageRange(ageRange)
                        .build();
        }

}
