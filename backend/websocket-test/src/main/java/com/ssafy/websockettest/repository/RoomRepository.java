package com.ssafy.websockettest.repository;

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
}
