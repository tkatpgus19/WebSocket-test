package com.ssafy.websockettest.model;

import lombok.Data;

import java.util.HashMap;
import java.util.UUID;

@Data
public class RoomDto {
    private String roomId;
    private String roomType;
    private String roomName;
    private boolean hasPassword;
    private String roomPassword;
    private String problemTier;
    private int problemNo;
    private String timeLimit;
    private String language;
    private boolean codeReview;
    private int UserCnt;
    private int maxUserCnt;
    private String master;
    private boolean isStarted;

    private HashMap<String, String> userList = new HashMap<>();
    private HashMap<String, String> readyList = new HashMap<>();

    public RoomDto() {
        this.roomId = UUID.randomUUID().toString();
        this.maxUserCnt = 6;
        this.isStarted = false;
    }
}
