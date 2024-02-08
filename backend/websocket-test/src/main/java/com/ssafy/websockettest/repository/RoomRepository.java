package com.ssafy.websockettest.repository;

import com.ssafy.websockettest.dto.request.PostRoomRequest;
import com.ssafy.websockettest.model.RoomDto;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Repository
@Getter
public class RoomRepository {
    private Map<String, RoomDto> normalRoomMap;
    private Map<String, RoomDto> itemRoomMap;

    @PostConstruct
    private void init(){
        normalRoomMap = new LinkedHashMap<>();
        itemRoomMap = new LinkedHashMap<>();
    }

    public String save(PostRoomRequest request){
        RoomDto room = RoomDto.builder()
                .roomType(request.getRoomType())
                .roomName(request.getRoomName())
                .hasPassword(request.getHasPassword())
                .roomPassword(request.getRoomPassword())
                .problemTier(request.getProblemTier())
                .problemNo(request.getProblemNo())
                .timeLimit(request.getTimeLimit())
                .language(request.getLanguage())
                .codeReview(request.getCodeReview())
                .master(request.getMaster())
                .build();

        if(request.getRoomType().equals("normal")){
            normalRoomMap.put(room.getRoomId(), room);
        }
        else{
            itemRoomMap.put(room.getRoomId(), room);
        }
        return room.getRoomId();
    }
}
