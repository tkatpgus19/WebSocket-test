package com.ssafy.websockettest.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class PostRoomResponse {
    private String roomId;

    @Builder
    private PostRoomResponse(String roomId) { this.roomId = roomId; }

    public static PostRoomResponse of(String roomId){
        return builder().
                roomId(roomId)
                .build();
    }
}
