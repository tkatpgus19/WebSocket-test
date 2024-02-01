package com.ssafy.websockettest.repository;

import com.ssafy.websockettest.model.ChatDto;
import com.ssafy.websockettest.model.ChatRoom;
import com.ssafy.websockettest.model.RoomDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Slf4j
public class ChatRoomRepository {
    private Map<String, ChatRoom> chatRoomMap;
    public Map<String, RoomDto> normalRoomMap;
    public Map<String, RoomDto> itemRoomMap;


    @PostConstruct
    private void init(){
        chatRoomMap = new LinkedHashMap<>();

        // 방 목록
        normalRoomMap = new LinkedHashMap<>();
        itemRoomMap = new LinkedHashMap<>();
    }

//    public List<ChatRoom> findAllRoom(){
//        // 채팅방 생성순서 순으로 반환
//        List<ChatRoom> chatRooms = new ArrayList<>(chatRoomMap.values());
//        Collections.reverse(chatRooms);
//        return chatRooms;
//    }

    public ChatRoom findRoomById(String roomId) {
        return chatRoomMap.get(roomId);
    }

    public ChatRoom createChatRoom(String roomName, int maxUserCnt, String roomPwd, boolean secretChk) {
        ChatRoom chatRoom = ChatRoom.create(roomName, maxUserCnt, roomPwd, secretChk);
        chatRoomMap.put(chatRoom.getRoomId(), chatRoom);
        log.warn("챗 룸 맵은 다음과 같다: "+chatRoomMap);
        return chatRoom;
    }

    public void plusUserCnt(ChatDto chatDto) {
        if(chatDto.getRoomType().equals("normal")){


            log.warn(chatDto.getRoomId());
            log.warn(normalRoomMap.toString());


            RoomDto room = normalRoomMap.get(chatDto.getRoomId());
            room.setUserCnt(room.getUserCnt()+1);
        }
        else{
            RoomDto room = itemRoomMap.get(chatDto.getRoomId());
            room.setUserCnt(room.getUserCnt()+1);
        }

    }

    public void minusUserCnt(String roomType, String roomId) {
        if(roomType.equals("normal")){
            RoomDto room = normalRoomMap.get(roomId);
            room.setUserCnt(room.getUserCnt() - 1);
        }
        else{
            RoomDto room = itemRoomMap.get(roomId);
            room.setUserCnt(room.getUserCnt() - 1);
        }
    }

    public String addUser(ChatDto chatDto){
        String userUUID = UUID.randomUUID().toString();
        if(chatDto.getRoomType().equals("normal")){
            RoomDto room = normalRoomMap.get(chatDto.getRoomId());

            if(room.getUserCnt()==1){
                // 맛스타 설정하기
            }
            room.getUserList().put(userUUID, chatDto.getSender());
        }
        else{
            RoomDto room = itemRoomMap.get(chatDto.getRoomId());
            if(room.getUserCnt()==1){
                // 맛스타 설정하기
            }
            room.getUserList().put(userUUID, chatDto.getSender());
        }

        return userUUID;
    }

    // 채팅방 유저 이름 중복 확인
    public String isDuplicateName(String roomId, String username){
        ChatRoom room = chatRoomMap.get(roomId);
        String tmp = username;

        // 만약 userName 이 중복이라면 랜덤한 숫자를 붙임
        // 이때 랜덤한 숫자를 붙였을 때 getUserlist 안에 있는 닉네임이라면 다시 랜덤한 숫자 붙이기!
        while(room.getUserList().containsValue(tmp)){
            int ranNum = (int) (Math.random()*100)+1;

            tmp = username+ranNum;
        }

        return tmp;
    }

    // 채팅방 유저 리스트 삭제
    public void delUser(String roomType, String roomId, String userUUID){
        if(roomType.equals("normal")){
            RoomDto room = normalRoomMap.get(roomId);
            room.getUserList().remove(userUUID);
        }
        else{
            RoomDto room = itemRoomMap.get(roomId);
            room.getUserList().remove(userUUID);
        }
    }

    // 채팅방 userName 조회
    public String getUserName(String roomType, String roomId, String userUUID){
        if(roomType.equals("normal")){
            RoomDto room = normalRoomMap.get(roomId);
            return room.getUserList().get(userUUID);
        }
        else{
            RoomDto room = normalRoomMap.get(roomId);
            return room.getUserList().get(userUUID);
        }
    }

    // 채팅방 전체 userlist 조회
    public ArrayList<String> getUserList(String roomType, String roomId){
        ArrayList<String> list = new ArrayList<>();

        if(roomType.equals("normal")){
            RoomDto room = normalRoomMap.get(roomId);
            room.getUserList().forEach((key, value) -> list.add(value));
        }
        else{
            RoomDto room = itemRoomMap.get(roomId);
            room.getUserList().forEach((key, value) -> list.add(value));
        }
        // hashmap 을 for 문을 돌린 후
        // value 값만 뽑아내서 list 에 저장 후 reutrn
//        room.getUserList().forEach((key, value) -> list.add(value));
        return list;
    }

    // maxUserCnt 에 따른 채팅방 입장 여부
    public boolean chkRoomUserCnt(String roomId){
        ChatRoom room = chatRoomMap.get(roomId);

        log.info("참여인원 확인 [{}, {}]", room.getUserCount(), room.getMaxUserCnt());

        if (room.getUserCount() + 1 > room.getMaxUserCnt()) {
            return false;
        }

        return true;
    }


    // 채팅방 비밀번호 조회
    public boolean confirmPwd(String roomId, String roomPwd) {
//        String pwd = chatRoomMap.get(roomId).getRoomPwd();
        return roomPwd.equals(chatRoomMap.get(roomId).getRoomPwd());
    }

    // 채팅방 삭제
    public void delChatRoom(String roomId) {
        try {
            // 채팅방 삭제
            chatRoomMap.remove(roomId);
            log.info("삭제 완료 roomId : {}", roomId);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
