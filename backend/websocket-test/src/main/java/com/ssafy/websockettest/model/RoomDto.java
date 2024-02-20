package com.ssafy.websockettest.model;

import lombok.Builder;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.UUID;

@Data
public class RoomDto {
    private String roomId;
    private String roomType;
    private String roomName;
    private Boolean hasPassword;
    private String roomPassword;
    private String problemTier;
    private String problemName;
    private Long timeLimit;
    private String language;
    private Boolean codeReview;
    private Integer userCnt;
    private Integer maxUserCnt;
    private String master;
    private Boolean isStarted;

    private LinkedHashMap<String, String> userList = new LinkedHashMap<>();
    private LinkedHashMap<String, String> readyList = new LinkedHashMap<>();

    @Builder
    public RoomDto(String roomId, String roomType, String roomName, Boolean hasPassword,
                   String roomPassword, String problemTier, String problemName, Long timeLimit,
                   String language, Boolean codeReview, Integer maxUserCnt,
                   String master, Boolean isStarted) {
        this.roomId = roomId != null ? roomId : UUID.randomUUID().toString();
        this.roomType = roomType;
        this.roomName = roomName;
        this.hasPassword = hasPassword;
        this.roomPassword = roomPassword;
        this.problemTier = problemTier;
        this.problemName = problemName;
        this.timeLimit = timeLimit;
        this.language = language;
        this.codeReview = codeReview;
        this.userCnt = 0;
        this.maxUserCnt = maxUserCnt != null ? maxUserCnt : 6;
        this.master = master;
        this.isStarted = isStarted != null ? isStarted : false;
    }

}
