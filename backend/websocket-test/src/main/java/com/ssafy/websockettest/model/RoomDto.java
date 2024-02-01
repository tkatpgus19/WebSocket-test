package com.ssafy.websockettest.model;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.UUID;

@Data
public class RoomDto {
    private String roomId;
    private String roomType;
    private String roomName;
    private boolean isLocked;
    private String roomPassword;
    private String problemTier;
    private int problemNo;
    private int timeLimit;
    private String language;
    private boolean hasReview;
    private int UserCnt;
    private int maxUserCnt;

    private HashMap<String, String> userList = new HashMap<>();

    public RoomDto() {
        this.roomId = UUID.randomUUID().toString();
        this.maxUserCnt = 6;
    }
}
