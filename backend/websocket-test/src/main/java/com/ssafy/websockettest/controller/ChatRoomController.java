package com.ssafy.websockettest.controller;

import com.ssafy.websockettest.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@CrossOrigin("*")
public class ChatRoomController {

    // chatRoomRepository Bean 가져오기
    private final ChatRoomRepository chatRoomRepository;


    // 채팅방 입장 화면
    // 파라미터로 넘어오는 roomId 를 확인후 해당 roomId 를 기준으로
    // 채팅방을 찾아서 클라이언트를 chatroom 으로 보낸다.
//    @GetMapping("/chat/room/{roomId}")
//    public ResponseEntity<?> roomDetail(@PathVariable("roomId") String roomId) {
//
//        log.info("roomId {}", roomId);
//        return new ResponseEntity<>(chatRoomRepository.findRoomById(roomId), HttpStatus.OK);
//    }

    // 채팅방 비밀번호 비교
    // 넘어오는 roomPwd 를 비교하고 일치하는지 체크 후 boolean 값을 반환한다.
    @PostMapping("/chat/checkPwd")
    public ResponseEntity<?> confirmPwd(@RequestParam("roomId") String roomId, @RequestParam("roomPwd") String roomPwd){
        return new ResponseEntity<>(chatRoomRepository.confirmPwd(roomId, roomPwd), HttpStatus.OK);
    }
}