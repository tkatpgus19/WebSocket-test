package com.ssafy.websockettest.controller;

import com.ssafy.websockettest.model.ChatDto;
import com.ssafy.websockettest.model.RoomDto;
import com.ssafy.websockettest.repository.RoomRepository;
import com.ssafy.websockettest.repository.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/rooms")
public class RoomController {
    private final SimpMessageSendingOperations template;

    private final RoomService roomService;

    // 방 등록
    @PostMapping("")
    public ResponseEntity<?> makeRoom(@RequestBody RoomDto roomDto){
        log.info("roomInfo {}", roomDto);
        roomService.save(roomDto);
        template.convertAndSend("/sub/normal/room-list", roomService.getNormalRoomList());
        template.convertAndSend("/sub/item/room-list", roomService.getItemRoomList());

        log.info("normalRoom Info: {}", roomService.getNormalRoomList());
        log.info("itemRoom info: {}", roomService.getItemRoomList());
        return new ResponseEntity<>(roomDto.getRoomId(), HttpStatus.OK);
    }

    // 노멀전 방 리스트 조회
    @GetMapping("/normal")
    public ResponseEntity<?> getNormalRoomList(){
        log.warn("노말룸 정보: {}", roomService.getNormalRoomList());
        return new ResponseEntity<>(roomService.getNormalRoomList(), HttpStatus.OK);
    }

    // 아이템전 방 리스트 조회
    @GetMapping("/item")
    public ResponseEntity<?> getItemRoomList(){
//        roomService.findRoomList("item", lang, tier);
        return new ResponseEntity<>(roomService.getItemRoomList(), HttpStatus.OK);
    }

    // 게임방 정보 조회
    @GetMapping("/info")
    public ResponseEntity<?> getRoomInfo(@RequestParam("roomType") String roomType, @RequestParam("roomId") String roomId){
        return new ResponseEntity<>(roomService.getRoomInfo(roomType, roomId), HttpStatus.OK);
    }

    // 유저 퇴장 시에는 EventListener 을 통해서 유저 퇴장을 확인
    @EventListener
    public void webSocketDisconnectListener(SessionDisconnectEvent event) {
        log.info("DisConnEvent {}", event);
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // stomp 세션에 있던 uuid 와 roomId 를 확인해서 채팅방 유저 리스트와 room 에서 해당 유저를 삭제
        String userUUID = (String) headerAccessor.getSessionAttributes().get("userUUID");
        String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");
        String roomType = (String) headerAccessor.getSessionAttributes().get("roomType");

        log.info("headAccessor {}", headerAccessor);

        // 채팅방 유저 리스트에서 UUID 유저 닉네임 조회 및 리스트에서 유저 삭제

        String username = roomService.getUserName(roomType, roomId, userUUID);
        roomService.delUser(roomType, roomId, userUUID);

        if (username != null) {
            log.info("User Disconnected : " + username);

            // builder 어노테이션 활용
            ChatDto chat = ChatDto.builder()
                    .type(ChatDto.MessageType.LEAVE)
                    .sender(username)
                    .message(username + " 님 퇴장!!")
                    .build();

            template.convertAndSend("/sub/chat/room/" + roomId, chat);
            template.convertAndSend("/sub/normal/room-list", roomService.getNormalRoomList());
            template.convertAndSend("/sub/item/room-list", roomService.getItemRoomList());
            if(roomService.getUserStatus(roomType, roomId) != null) {
                template.convertAndSend("/sub/room/" + roomId + "/status", roomService.getUserStatus(roomType, roomId));
            }
        }
    }

    @MessageMapping("/chat/enterUser")
    public void enterUser(@Payload ChatDto chat, SimpMessageHeaderAccessor headerAccessor) {
        // 채팅방에 유저 추가 및 UserUUID 반환
        String userUUID = roomService.addUser(chat);

        // 반환 결과를 socket session 에 userUUID 로 저장
        headerAccessor.getSessionAttributes().put("userUUID", userUUID);
        headerAccessor.getSessionAttributes().put("roomId", chat.getRoomId());
        headerAccessor.getSessionAttributes().put("roomType", chat.getRoomType());

        chat.setMessage(chat.getSender() + " 님 입장!!");
        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);
        template.convertAndSend("/sub/room/"+chat.getRoomId()+"/status", roomService.getUserStatus(chat.getRoomType(), chat.getRoomId()));

        template.convertAndSend("/sub/normal/room-list", roomService.getNormalRoomList());
        template.convertAndSend("/sub/item/room-list", roomService.getItemRoomList());
    }

    // 게임에 참여한 유저 리스트 및 상태 반환
    @GetMapping("/userStatus")
    public ResponseEntity<?> userList(@RequestParam("roomType") String roomType, @RequestParam("roomId") String roomId) {
        return new ResponseEntity<>(roomService.getUserStatus(roomType, roomId), HttpStatus.OK);
    }

    // 게임방 채팅
    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload ChatDto chat) {
        log.info("개인 채팅 : " + chat.getMessage());
        chat.setMessage(chat.getMessage());
        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);
    }

    // 전체 채팅
    @MessageMapping("/chat/all/sendMessage")
    public void sendMessage2All(@Payload ChatDto chat) {
        log.info("전체 채팅 : " + chat.getMessage());
        chat.setMessage(chat.getMessage());
        template.convertAndSend("/sub/chat/all", chat);
    }

    @PutMapping("/ready")
    public void ready(@RequestBody ChatDto chat){
        roomService.ready(chat);
        template.convertAndSend("/sub/room/"+chat.getRoomId()+"/status", roomService.getUserStatus(chat.getRoomType(), chat.getRoomId()));
    }
}
