package com.ssafy.websockettest.repository;

import com.ssafy.websockettest.dto.request.PostRoomRequest;
import com.ssafy.websockettest.model.RoomDto;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@Getter
public class RoomRepository {
    private Map<String, RoomDto> roomMap;

    @PostConstruct
    private void init(){
        roomMap = new LinkedHashMap<>();
    }

    public String saveRoom(PostRoomRequest request){
        RoomDto room = RoomDto.builder()
                .roomType(request.getRoomType())
                .roomName(request.getRoomName())
                .hasPassword(request.getHasPassword())
                .roomPassword(request.getRoomPassword())
                .problemTier(request.getProblemTier())
                .problemName(request.getProblemName())
                .timeLimit(request.getTimeLimit())
                .language(request.getLanguage())
                .codeReview(request.getCodeReview())
                .master(request.getMaster())
                .build();
        roomMap.put(room.getRoomId(), room);
        return room.getRoomId();
    }

    public List<RoomDto> getRoomListByRoomType(String roomType){
        return roomMap.values()
                .stream()
                .filter(entry -> entry.getRoomType().equals(roomType))
                .toList();
    }

    public List<RoomDto> getRoomList(){
        return roomMap.values().stream().toList();
    }

    public RoomDto getRoomById(String roomId){
        return roomMap.getOrDefault(roomId, null);
    }
}
