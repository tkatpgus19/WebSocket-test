package com.ssafy.websockettest.model;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.UUID;

@Data
public class ChatRoom {
    private String roomId;
    private String roomName;
    private long userCount;
    private int maxUserCnt;

    private String roomPwd;
    private boolean secretChk;

    private HashMap<String, String> userList = new HashMap<>();
    public static ChatRoom create(String roomName, int maxUserCnt, String roomPwd, boolean secretChk){
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = UUID.randomUUID().toString();
        chatRoom.roomName = roomName;
        chatRoom.maxUserCnt = maxUserCnt;
        chatRoom.roomPwd = roomPwd;
        chatRoom.secretChk = secretChk;
        return chatRoom;
    }
}
