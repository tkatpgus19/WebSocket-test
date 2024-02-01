package com.ssafy.websockettest.controller;

import com.ssafy.websockettest.model.RoomDto;
import com.ssafy.websockettest.repository.RoomRepository;
import com.ssafy.websockettest.repository.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
//@RequestMapping("/rooms")
public class RoomController {
    private final SimpMessageSendingOperations template;

    private final RoomService roomService;

//    @PostMapping("")
//    public ResponseEntity<?> makeRoom(@RequestBody RoomDto roomDto){
//        log.info("roomInfo {}", roomDto);
//        roomService.save(roomDto);
//        template.convertAndSend("/sub/normal/room-list", roomService.getNormalRoomList());
//        template.convertAndSend("/sub/item/room-list", roomService.getItemRoomList());
//
//        log.info("normalRoom Info: {}", roomService.getNormalRoomList());
//        log.info("itemRoom info: {}", roomService.getItemRoomList());
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    @GetMapping("/normal")
//    public ResponseEntity<?> getNormalRoomList(){
//        return new ResponseEntity<>(roomService.getNormalRoomList(), HttpStatus.OK);
//    }
//
//    @GetMapping("/item")
//    public ResponseEntity<?> getItemRoomList(){
//        return new ResponseEntity<>(roomService.getItemRoomList(), HttpStatus.OK);
//    }
}
