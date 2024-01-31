package com.ssafy.websockettest.repository;

import com.ssafy.websockettest.model.RoomDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final ChatRoomRepository chatRoomRepository;
    public void save(RoomDto roomDto){
        chatRoomRepository.roomList.add(roomDto);
    }
}
