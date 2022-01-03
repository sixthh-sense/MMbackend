package com.sparta.mbti.controller;

import com.sparta.mbti.model.ChatRoom;
import com.sparta.mbti.model.User;
import com.sparta.mbti.repository.UserRepository;
import com.sparta.mbti.security.UserDetailsImpl;
import com.sparta.mbti.security.jwt.JwtTokenUtils;
import com.sparta.mbti.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController{

    private final ChatRoomService chatRoomService;
    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtils;

    // 채팅 리스트 화면
    @GetMapping("/room")
    public String rooms(Model model) {
        return "/chat/room";
    }
    // 모든 채팅방 목록 반환
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoom> room() {
        return chatRoomService.findAllRoom();
    }
    // 채팅방 생성
    @PostMapping("/room")
    @ResponseBody
    public ChatRoom createRoom(@RequestParam String name, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(() -> new IllegalArgumentException("유저가 없습니다"));
        System.out.println(name);
        System.out.println(user.getNickname());
        return chatRoomService.createChatRoom(name, user);
    }
    // 채팅방 입장 화면
    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "/chat/roomdetail";
    }
    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoom roomInfo(@PathVariable String roomId) {
        return chatRoomService.findRoomById(roomId);
    }


}