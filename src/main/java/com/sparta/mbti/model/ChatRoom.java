package com.sparta.mbti.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ChatRoom implements Serializable {

    private static final long serialVersionUID = 588493380454865071L;
    // 임의로 설정하는 건 줄 알았는데 따로 정해진 거였나? 위의 숫자가 아니라 다른 숫자로 했더니 error가 떠서 error창에 나온 숫자로 다시 쓰니 "해당 error"는 지워짐

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String roomId;

    @Column
    private String name;

    @Column
    private Long ownerId; // 채팅방 만든 user의 id(pk). 한마디로 방장.

    public static ChatRoom create(String name, User user) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = UUID.randomUUID().toString();
        chatRoom.name = name;
        chatRoom.ownerId = user.getId();
        return chatRoom;
    }
}