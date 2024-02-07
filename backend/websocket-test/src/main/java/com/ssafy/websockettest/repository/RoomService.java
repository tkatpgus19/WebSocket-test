package com.ssafy.websockettest.repository;

import com.ssafy.websockettest.model.ChatDto;
import com.ssafy.websockettest.model.RoomDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
    public List<RoomDto> getNormalRoomList(String language, String tier, Integer page){
        List<RoomDto> resultList = roomRepository.getNormalRoomMap().values().stream().toList();
        if(language != null){
            resultList = resultList
                    .stream()
                    .filter(entry -> entry.getLanguage().equals(language))
                    .toList();
        }
        if(tier != null){
            resultList = resultList
                    .stream()
                    .filter(entry -> entry.getProblemTier().equals(tier))
                    .toList();
        }
        if(resultList.size() > (page-1)*4){
            if(resultList.size()<(page-1)*4+4){
                resultList = resultList.subList((page-1)*4, resultList.size());
            }
            else{
                resultList = resultList.subList((page-1)*4, (page-1)*4+4);
            }
        }
        else{
            return Collections.emptyList();
        }
        return resultList;
    }
    
    // 아이템전 방 리스트 조회
    public List<RoomDto> getItemRoomList(String language, String tier, Integer page){
        List<RoomDto> resultList = roomRepository.getItemRoomMap().values().stream().toList();
        if(language != null){
            resultList = resultList
                    .stream()
                    .filter(entry -> entry.getLanguage().equals(language))
                    .toList();
        }
        if(tier != null){
            resultList = resultList
                    .stream()
                    .filter(entry -> entry.getProblemTier().equals(tier))
                    .toList();
        }
//        if(resultList.size() > (page-1)*4){
//            if(resultList.size()<(page-1)*4+4){
//                resultList = resultList.subList((page-1)*4, resultList.size());
//            }
//            else{
//                resultList = resultList.subList((page-1)*4, (page-1)*4+4);
//            }
//        }
//        else{
//            return Collections.emptyList();
//        }
        return resultList;
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
                room.getReadyList().put(user, "MASTER");
            }
            // 참가자 대기상태 설정
            else {
                room.getReadyList().put(user, "WAITING");
            }
        }
        else{
            RoomDto room = roomRepository.getItemRoomMap().get(chatDto.getRoomId());
            room.setUserCnt(room.getUserCnt()+1);
            room.getUserList().put(userUUID, user);

            // 마스터 등록
            if(room.getUserCnt()==1){
                room.getReadyList().put(user, "MASTER");
            }
            // 참가자들 추가 및 대기상태 설정
            else {
                room.getReadyList().put(user, "WAITING");
            }
        }
        return userUUID;
    }

    // 방에서 인원 삭제
    public void delUser(String roomType, String roomId, String userUUID){
        if(roomType != null) {
            if (roomType.equals("normal")) {
                RoomDto room = roomRepository.getNormalRoomMap().get(roomId);
                room.setUserCnt(room.getUserCnt() - 1);
                String user = room.getUserList().get(userUUID);

                if(room.getReadyList().get(user).equals("MASTER") && room.getUserCnt() != 0){
                    room.getReadyList().remove(user);
                    Map.Entry<String, String> firstEntry = room.getReadyList().entrySet().iterator().next();
                    room.getReadyList().replace(firstEntry.getKey(), "MASTER");
                    room.setMaster(firstEntry.getKey());
                }
                room.getUserList().remove(userUUID);

                if (room.getUserCnt() == 0) {
                    roomRepository.getNormalRoomMap().remove(roomId);
                }
            } else {
                RoomDto room = roomRepository.getItemRoomMap().get(roomId);
                room.setUserCnt(room.getUserCnt() - 1);
                String user = room.getUserList().get(userUUID);

                room.getUserList().remove(userUUID);
                room.getReadyList().remove(user);

                if (room.getUserCnt() == 0) {
                    roomRepository.getItemRoomMap().remove(roomId);
                }
            }
        }
    }

    // 게임방 참여인원 조회
    public LinkedHashMap<String, String> getUserStatus(String roomType, String roomId){
        if(roomType.equals("normal")){
            if(roomRepository.getNormalRoomMap().getOrDefault(roomId, null) != null) {
                return roomRepository.getNormalRoomMap().get(roomId).getReadyList();
            }
        }
        else{
            if(roomRepository.getItemRoomMap().getOrDefault(roomId, null) != null) {
                return roomRepository.getItemRoomMap().get(roomId).getReadyList();
            }
        }
        return null;
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
        if(roomType != null) {
            if (roomType.equals("normal")) {
                RoomDto room = roomRepository.getNormalRoomMap().get(roomId);
                return room.getUserList().get(userUUID);
            } else {
                RoomDto room = roomRepository.getItemRoomMap().get(roomId);
                return room.getUserList().get(userUUID);
            }
        }
        return null;
    }

    public void ready(ChatDto chat){
        if(chat.getRoomType().equals("normal")){
            RoomDto room = roomRepository.getNormalRoomMap().get(chat.getRoomId());
            String status = room.getReadyList().get(chat.getSender());
            if(status.equals("MASTER")){

            }
            else if(status.equals("WAITING")){
                room.getReadyList().replace(chat.getSender(), "READY");
            }
            else{
                room.getReadyList().replace(chat.getSender(), "WAITING");
            }
        }
        else{
            RoomDto room = roomRepository.getItemRoomMap().get(chat.getRoomId());
            String status = room.getReadyList().get(chat.getSender());
            if(status.equals("MASTER")){

            }
            else if(status.equals("WAITING")){
                room.getReadyList().replace(chat.getSender(), "READY");
            }
            else{
                room.getReadyList().replace(chat.getSender(), "WAITING");
            }
        }
    }

    public Boolean checkPwd(String roomType, String roomId, String password){
        if(roomType.equals("normal")){
            RoomDto room = roomRepository.getNormalRoomMap().get(roomId);
            return room.getRoomPassword().equals(password);
        }
        else{
            RoomDto room = roomRepository.getItemRoomMap().get(roomId);
            return room.getRoomPassword().equals(password);
        }
    }

    public Boolean checkPersonnel(String roomType, String roomId){
        if(roomType.equals("normal")){
            RoomDto room = roomRepository.getNormalRoomMap().get(roomId);
            return room.getUserCnt() != room.getMaxUserCnt();
        }
        else{
            RoomDto room = roomRepository.getItemRoomMap().get(roomId);
            return room.getUserCnt() != room.getMaxUserCnt();
        }
    }

    public RoomDto checkReady(String roomType, String roomId){
        int cnt = 0;
        if(roomType.equals("normal")){
            RoomDto room = roomRepository.getNormalRoomMap().get(roomId);
            List<String> list = room
                    .getReadyList()
                    .values()
                    .stream()
                    .toList();
            for(String status: list){
                if(status.equals("READY")){
                    cnt++;
                }
            }
            if(cnt == room.getUserCnt()-1){
                room.setStarted(true);
                return room;
            }
            return null;

        }
        else{
            RoomDto room = roomRepository.getItemRoomMap().get(roomId);
            List<String> list = room
                    .getReadyList()
                    .values()
                    .stream()
                    .toList();
            for(String status: list){
                if(status.equals("READY")){
                    cnt++;
                }
            }
            if(cnt == room.getUserCnt()-1){
                return room;
            }
            return null;
        }
    }
}
