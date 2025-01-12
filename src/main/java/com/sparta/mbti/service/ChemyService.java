package com.sparta.mbti.service;

import com.sparta.mbti.dto.ChemyUserResponseDto;
import com.sparta.mbti.dto.InterestListDto;
import com.sparta.mbti.model.Mbti;
import com.sparta.mbti.model.User;
import com.sparta.mbti.model.UserInterest;
import com.sparta.mbti.repository.MbtiRepository;
import com.sparta.mbti.repository.UserInterestRepository;
import com.sparta.mbti.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class ChemyService {
    private final MbtiRepository mbtiRepository;
    private final UserRepository userRepository;
    private final UserInterestRepository userInterestRepository;

    // 자동 매칭
    @Transactional
    public ChemyUserResponseDto chemyAuto(User user) {
        // 사용자 가장 이상적 MBTI 조회
        Mbti findMbti = mbtiRepository.findByMbtiFirst(user.getMbti().getMbti()).orElseThrow(
                () -> new IllegalArgumentException("해당 MBTI가 존재하지 않습니다.")
        );

        // 위치 / MBTI 와 가장 이상적인 사용자 리스트 조회
        List<User> findUserList = userRepository.findAllByLocationAndMbti(user.getLocation(), findMbti);
        // 사용자 리스트 수 범위만큼 랜덤 생성 (10 이면 0~9 랜덤 생성)
        Random generator = new Random();
        int size = generator.nextInt(findUserList.size());

        // 랜덤 사용자 관심사 리스트 조회
        List<UserInterest> userInterestList = userInterestRepository.findAllByUser(findUserList.get(size));
        List<InterestListDto> interestList = new ArrayList<>();
        for (UserInterest userInterest : userInterestList) {
            interestList.add(InterestListDto.builder()
                    .interest(userInterest.getInterest().getInterest())
                    .build());
        }

        // 랜덤 사용자 반환
        return ChemyUserResponseDto.builder()
                .userId(findUserList.get(size).getId())
                .nickname(findUserList.get(size).getNickname())
                .profileImage(findUserList.get(size).getProfileImage())
                .gender(findUserList.get(size).getGender())
                .ageRange(findUserList.get(size).getAgeRange())
                .intro(findUserList.get(size).getIntro())
                .location(findUserList.get(size).getLocation().getLocation())
                .mbti(findUserList.get(size).getMbti().getMbti())
                .interestList(interestList)
                .build();
    }

    // 케미 사용자 선택
    @Transactional
    public ChemyUserResponseDto chemyUser(Long userId) {
        // 사용자 조회
        User findUser = userRepository.getById(userId);

        // 관심사 리스트 조회
        List<UserInterest> userInterestList = userInterestRepository.findAllByUser(findUser);
        List<InterestListDto> interestList = new ArrayList<>();
        for (UserInterest userInterest : userInterestList) {
            interestList.add(InterestListDto.builder()
                                    .interest(userInterest.getInterest().getInterest())
                                    .build());
        }

        // 반환
        return ChemyUserResponseDto.builder()
                .userId(findUser.getId())
                .nickname(findUser.getNickname())
                .profileImage(findUser.getProfileImage())
                .gender(findUser.getGender())
                .ageRange(findUser.getAgeRange())
                .intro(findUser.getIntro())
                .location(findUser.getLocation().getLocation())
                .mbti(findUser.getMbti().getMbti())
                .interestList(interestList)
                .build();
    }
}
