package com.sparta.mbti.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.mbti.dto.UserRequestDto;
import com.sparta.mbti.dto.UserResponseDto;
import com.sparta.mbti.security.UserDetailsImpl;
import com.sparta.mbti.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    // 카카오 로그인
    @GetMapping("/user/kakao/callback")
    public UserResponseDto kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        // 카카오 서버로부터 받은 인가 코드, JWT 토큰
        return userService.kakaoLogin(code, response);
    }

    // 내정보 입력 / 수정
    @PutMapping("/api/profile")
    public void updateProfile(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody  UserRequestDto userRequestDto) {
        // 추가 정보 입력
        userService.updateProfile(userDetails.getUser(), userRequestDto);
    }

}
