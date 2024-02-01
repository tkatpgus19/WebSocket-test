package com.ssafy.websockettest.repository;

import com.ssafy.websockettest.model.RoomDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final ChatRoomRepository chatRoomRepository;
    public void save(RoomDto roomDto){
        if(roomDto.getRoomType().equals("normal")){
            chatRoomRepository.normalRoomMap.put(roomDto.getRoomId(), roomDto);
        }
        else{
            chatRoomRepository.itemRoomMap.put(roomDto.getRoomId(), roomDto);
        }
    }
}
