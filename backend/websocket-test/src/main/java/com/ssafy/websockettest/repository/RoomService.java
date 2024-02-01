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

    private final RoomRepository roomRepository;
    public void save(RoomDto roomDto){
        if(roomDto.getRoomType().equals("normal")){
            roomRepository.getNormalRoomMap().put(roomDto.getRoomId(), roomDto);
        }
        else{
            roomRepository.getItemRoomMap().put(roomDto.getRoomId(), roomDto);
        }
    }

    public List<RoomDto> getNormalRoomList(){
        return roomRepository.getNormalRoomMap().values().stream().toList();
    }
    public List<RoomDto> getItemRoomList(){
        return roomRepository.getItemRoomMap().values().stream().toList();
    }
}
