package com.ssafy.websockettest.controller;

import com.ssafy.websockettest.model.ChatRoom;
import com.ssafy.websockettest.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
@RestController
@CrossOrigin("*")
public class ChatRoomController {

    // chatRoomRepository Bean 가져오기
    private final ChatRoomRepository chatRoomRepository;

    // 채팅 리스트 화면
    // / 로 요청이 들어오면 전체 채팅룸 리스트를 담아서 return
    @GetMapping("/")
    public ResponseEntity<?> goChatRoom() {
        log.info("SHOW ALL ChatList {}", chatRoomRepository.findAllRoom());
        return new ResponseEntity<>(chatRoomRepository.findAllRoom(), HttpStatus.OK);
    }

    // 채팅방 생성
    // 채팅방 생성 후 다시 / 로 return
    @PostMapping("/chat/createroom")
    public ResponseEntity<?> createRoom(@RequestParam("name") String name) {
        ChatRoom room = chatRoomRepository.createChatRoom(name);
        log.info("CREATE Chat Room {}", room);
        return new ResponseEntity<>("roomName" + room, HttpStatus.OK);
    }

    // 채팅방 입장 화면
    // 파라미터로 넘어오는 roomId 를 확인후 해당 roomId 를 기준으로
    // 채팅방을 찾아서 클라이언트를 chatroom 으로 보낸다.
    @GetMapping("/chat/room/{roomId}")
    public ResponseEntity<?> roomDetail(@PathVariable String roomId) {

        log.info("roomId {}", roomId);
        return new ResponseEntity<>(chatRoomRepository.findRoomById(roomId), HttpStatus.OK);
    }
}