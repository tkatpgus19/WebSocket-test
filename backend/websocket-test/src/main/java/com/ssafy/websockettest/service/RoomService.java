package com.ssafy.websockettest.service;

import com.ssafy.websockettest.dto.request.DeleteRoomRequest;
import com.ssafy.websockettest.dto.request.PostRoomEnterRequest;
import com.ssafy.websockettest.dto.request.PostRoomRequest;
import com.ssafy.websockettest.dto.request.PutReadyRequest;
import com.ssafy.websockettest.dto.response.PostRoomResponse;
import com.ssafy.websockettest.model.ChatDto;
import com.ssafy.websockettest.model.RoomDto;
import com.ssafy.websockettest.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.getSessionAttributes;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final SimpMessageSendingOperations template;

    // 방 등록
    public PostRoomResponse post(PostRoomRequest request){
        String roomId = roomRepository.saveRoom(request);

        template.convertAndSend("/sub/normal/room-list", roomRepository.getRoomListByRoomType("normal"));
        template.convertAndSend("/sub/item/room-list", roomRepository.getRoomListByRoomType("item"));

        // 생성된 방 ID 값 반환
        return PostRoomResponse.of(roomId);
    }

    // 조건에 부합하는 방 리스트 조회
    public List<RoomDto> getSortedRoomList(String roomType, String language, String tier, Boolean codeReview, Boolean isSolved, Integer page){
        List<RoomDto> resultList = roomRepository.getRoomListByRoomType(roomType);
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
        if(codeReview != null){
            resultList = resultList
                    .stream()
                    .filter(entry -> entry.getCodeReview() == codeReview)
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

    public Boolean enter(PostRoomEnterRequest request){
        RoomDto room = roomRepository.getRoomById(request.getRoomId());
        if(room.getUserCnt() < room.getMaxUserCnt()){
            String userUUID = UUID.randomUUID().toString();
            room.setUserCnt(room.getUserCnt()+1);
            room.getUserList().put(userUUID, request.getNickname());

            // 마스터 등록
            if(room.getUserCnt()==1){
                room.getReadyList().put(request.getNickname(), "MASTER");
            }
            // 참가자 대기상태 설정
            else {
                room.getReadyList().put(request.getNickname(), "WAITING");
            }

            ChatDto chat = new ChatDto();
            chat.setSender(request.getNickname());
            chat.setMessage(chat.getSender() + " 님 입장!!");

            Message<ChatDto> message = MessageBuilder
                    .withPayload(chat)
                    .setHeader("userUUID", userUUID)
                    .setHeader("roomId", request.getRoomId())
                    .build();

            template.convertAndSend("/sub/chat/room/" + request.getRoomId(), message);
            template.convertAndSend("/sub/room/"+request.getRoomId()+"/status", getUserStatus(request.getRoomId()));

            template.convertAndSend("/sub/normal/room-list", getSortedRoomList("normal",null, null, null, null, 1));
            template.convertAndSend("/sub/item/room-list", getSortedRoomList("item", null, null, null, null,1));
            return true;
        }
        return false;
    }
    // 방에 인원 추가
    public String addUser(String roomId, String nickname){
        String userUUID = UUID.randomUUID().toString();

        RoomDto room = roomRepository.getRoomById(roomId);
        room.setUserCnt(room.getUserCnt()+1);
        room.getUserList().put(userUUID, nickname);

        // 마스터 등록
        if(room.getUserCnt()==1){
            room.getReadyList().put(nickname, "MASTER");
        }
        // 참가자 대기상태 설정
        else {
            room.getReadyList().put(nickname, "WAITING");
        }

        return userUUID;
    }

    // 방에서 인원 삭제
    public void delUser(String roomId, String userUUID){
        if(roomId != null) {
            RoomDto room = roomRepository.getRoomById(roomId);
            room.setUserCnt(room.getUserCnt() - 1);
            String user = room.getUserList().get(userUUID);

            if (room.getReadyList().get(user).equals("MASTER") && room.getUserCnt() != 0) {
                room.getReadyList().remove(user);
                Map.Entry<String, String> firstEntry = room.getReadyList().entrySet().iterator().next();
                room.getReadyList().replace(firstEntry.getKey(), "MASTER");
                room.setMaster(firstEntry.getKey());
            } else {
                room.getReadyList().remove(user);
            }
            room.getUserList().remove(userUUID);

            if (room.getUserCnt() == 0) {
                roomRepository.getRoomMap().remove(roomId);
            }
        }
        clearRooms();
    }

    // 게임방 참여인원 조회
    public LinkedHashMap<String, String> getUserStatus(String roomId){
        if(roomRepository.getRoomById(roomId) != null){
            return roomRepository.getRoomById(roomId).getReadyList();
        }
        return null;
    }

    // 게임방 정보 조회
    public RoomDto getRoomInfo(String roomId){
        return roomRepository.getRoomById(roomId);
    }

    // 채팅방 userName 조회
    public String getUserName(String roomId, String userUUID){
        if(roomId != null) {
            RoomDto room = roomRepository.getRoomById(roomId);
            return room.getUserList().get(userUUID);
        }
        return null;
    }

    public Boolean ready(PutReadyRequest request){
        RoomDto room = roomRepository.getRoomById(request.getRoomId());
        String status = room.getReadyList().get(request.getNickname());
        if(status.equals("WAITING")){
            room.getReadyList().replace(request.getNickname(), "READY");
        }
        else{
            room.getReadyList().replace(request.getNickname(), "WAITING");
        }
        template.convertAndSend("/sub/room/"+request.getRoomId()+"/status", getUserStatus(request.getRoomId()));
        return true;
    }

    public Boolean checkPwd(String roomId, String password){
        RoomDto room = roomRepository.getRoomById(roomId);
        return room.getRoomPassword().equals(password);
    }

    //
    public Boolean checkPersonnel(String roomId){
        RoomDto room = roomRepository.getRoomById(roomId);
        return !Objects.equals(room.getUserCnt(), room.getMaxUserCnt());
    }

    public Boolean checkReady(String roomId){
        int cnt = 0;
        RoomDto room = roomRepository.getRoomById(roomId);
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
            room.setIsStarted(true);
            template.convertAndSend("/sub/room/"+roomId+"/start", room);
            return true;
        }
        return false;
    }

    public void clearRooms(){
        for(RoomDto room : roomRepository.getRoomList()){
            if(room.getUserCnt() == 0){
                roomRepository.getRoomMap().remove(room.getRoomId());
            }
        }
    }

    public Boolean startTimer(String roomId){
        long timerValue;
        for (int i = 10; i >= 0; i--) {
            timerValue = i;
            template.convertAndSend("/sub/timer/"+roomId, timerValue);
            log.warn("초: " + timerValue);
            try {
                Thread.sleep(1000); // 1초 대기
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public Boolean delete(DeleteRoomRequest request){
        if(roomRepository.getRoomMap().getOrDefault(request.getRoomId(), null) != null) {
            roomRepository.getRoomMap().remove(request.getRoomId());
            template.convertAndSend("/sub/room/" + request.getRoomId() + "/finished", true);
            template.convertAndSend("/sub/normal/room-list", roomRepository.getRoomListByRoomType("normal"));
            template.convertAndSend("/sub/item/room-list", roomRepository.getRoomListByRoomType("item"));
            return true;
        }
        return false;
    }
}
