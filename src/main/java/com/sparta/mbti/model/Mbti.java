package com.sparta.mbti.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Mbti {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MBTI_ID")
    private Long id;                            // 테이블 기본키

    @Column
    private String mbti;                        // mbti

    @Column
    private String mbtiFirst;                   // mbti 이상적 1단계

    @Column
    private String mbtiSecond;                  // mbti 이상적 2단계

    @Column
    private String mbtiThird;                   // mbti 이상적 3단계

    @Column
    private String mbtiForth;                   // mbti 이상적 4단계
}
