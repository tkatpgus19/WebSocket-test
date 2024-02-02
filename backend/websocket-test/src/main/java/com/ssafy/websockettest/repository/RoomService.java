package com.ssafy.websockettest.repository;

import com.ssafy.websockettest.model.ChatDto;
import com.ssafy.websockettest.model.RoomDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    
    // 방 등록
    public void save(RoomDto roomDto){
        if(roomDto.getRoomType().equals("normal")){
            roomRepository.getNormalRoomMap().put(roomDto.getRoomId(), roomDto);
        }
        else{
            roomRepository.getItemRoomMap().put(roomDto.getRoomId(), roomDto);
        }
    }

    // 노말전 방 리스트 조회
    public List<RoomDto> getNormalRoomList(){
        return roomRepository.getNormalRoomMap().values().stream().toList();
    }
    
    // 아이템전 방 리스트 조회
    public List<RoomDto> getItemRoomList(){
        return roomRepository.getItemRoomMap().values().stream().toList();
    }

    // 방에 인원 추가
    public String addUser(ChatDto chatDto){
        String userUUID = UUID.randomUUID().toString();
        String user = chatDto.getSender();

        if(chatDto.getRoomType().equals("normal")){
            RoomDto room = roomRepository.getNormalRoomMap().get(chatDto.getRoomId());
            room.setUserCnt(room.getUserCnt()+1);
            room.getUserList().put(userUUID, user);

            // 마스터 등록
            if(room.getUserCnt()==1){
                room.getReadyList().put(user, "wait");
            }
            // 참가자 대기상태 설정
            else {
                room.getReadyList().put(user, "waiting");
            }
        }
        else{
            RoomDto room = roomRepository.getItemRoomMap().get(chatDto.getRoomId());
            room.setUserCnt(room.getUserCnt()+1);
            room.getUserList().put(userUUID, user);

            // 마스터 등록
            if(room.getUserCnt()==1){
                room.getReadyList().put(user, "wait");
            }
            // 참가자들 추가 및 대기상태 설정
            else {
                room.getReadyList().put(user, "waiting");
            }
        }
        return userUUID;
    }

    // 방에서 인원 삭제
    public void delUser(String roomType, String roomId, String userUUID){
        if(roomType.equals("normal")){
            RoomDto room = roomRepository.getNormalRoomMap().get(roomId);
            room.setUserCnt(room.getUserCnt()-1);
            String user = room.getUserList().get(userUUID);

            room.getUserList().remove(userUUID);
            room.getReadyList().remove(user);

            if(room.getUserCnt() == 0){
                roomRepository.getNormalRoomMap().remove(roomId);
            }
        }
        else{
            RoomDto room = roomRepository.getItemRoomMap().get(roomId);
            room.setUserCnt(room.getUserCnt()-1);
            String user = room.getUserList().get(userUUID);

            room.getUserList().remove(userUUID);
            room.getReadyList().remove(user);

            if(room.getUserCnt() == 0){
                roomRepository.getNormalRoomMap().remove(roomId);
            }
        }
    }

    // 게임방 참여인원 조회
    public HashMap<String, String> getUserStatus(String roomType, String roomId){
        if(roomType.equals("normal")){
            return roomRepository.getNormalRoomMap().get(roomId).getReadyList();
        }
        else{
            return roomRepository.getItemRoomMap().get(roomId).getReadyList();
        }
    }

    public RoomDto getRoomInfo(String roomType, String roomId){
        if(roomType.equals("normal")){
            return roomRepository.getNormalRoomMap().get(roomId);
        }
        else{
            return roomRepository.getItemRoomMap().get(roomId);
        }
    }

    // 채팅방 userName 조회
    public String getUserName(String roomType, String roomId, String userUUID){
        if(roomType.equals("normal")){
            RoomDto room = roomRepository.getNormalRoomMap().get(roomId);
            return room.getUserList().get(userUUID);
        }
        else{
            RoomDto room = roomRepository.getItemRoomMap().get(roomId);
            return room.getUserList().get(userUUID);
        }
    }

}
