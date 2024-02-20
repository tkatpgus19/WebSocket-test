package com.ssafy.websockettest.controller;

import com.ssafy.websockettest.common.exception.CustomBadRequestException;
import com.ssafy.websockettest.common.response.BaseSuccessResponse;
import com.ssafy.websockettest.dto.request.DeleteRoomRequest;
import com.ssafy.websockettest.dto.request.PostRoomEnterRequest;
import com.ssafy.websockettest.dto.request.PostRoomRequest;
import com.ssafy.websockettest.dto.request.PutReadyRequest;
import com.ssafy.websockettest.model.ChatDto;
import com.ssafy.websockettest.model.ItemDto;
import com.ssafy.websockettest.service.RoomService;
import jakarta.validation.Valid;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import static com.ssafy.websockettest.common.response.BaseResponseStatus.*;
import static com.ssafy.websockettest.common.util.BindingResultUtils.getErrorMessages;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {
    private final SimpMessageSendingOperations template;
    private final RoomService roomService;

    // 방 등록
    @PostMapping("")
    public BaseSuccessResponse<?> post(@RequestBody @Valid PostRoomRequest request, BindingResult bindingResult){
        log.info("RoomController.post\nRoomInfo: {}" ,request);

        // validation 오류
        if(bindingResult.hasErrors()){
            throw new CustomBadRequestException(BAD_REQUEST, getErrorMessages(bindingResult));
        }

        return BaseSuccessResponse.of(POST_ROOM_SUCCESS, roomService.post(request));
    }

    // 노멀전 방 리스트 조회
    @GetMapping("/normal")
    public BaseSuccessResponse<?> getNormalRoomList(@RequestParam(value = "language", required = false) String language,
                                                    @RequestParam(value = "tier", required = false) String tier,
                                                    @RequestParam(value = "has-review", required = false) Boolean codeReview,
                                                    @RequestParam(value = "is-solved", required = false) Boolean isSolved,
                                                    @RequestParam(value = "page", required = false) Integer page){
        log.warn("노말전 방 정보: {}", roomService.getSortedRoomList("normal", language, tier, codeReview, isSolved, page));
        return BaseSuccessResponse.of(GET_ROOM_LIST_SUCCESS, roomService.getSortedRoomList("normal", language, tier, codeReview, isSolved, page));
    }

    // 아이템전 방 리스트 조회
    @GetMapping("/item")
    public BaseSuccessResponse<?> getItemRoomList(@RequestParam(value = "language", required = false) String language,
                                                  @RequestParam(value = "tier", required = false) String tier,
                                                  @RequestParam(value = "page", required = false) Integer page){
        log.warn("아이템전 방 정보: {}", roomService.getSortedRoomList("item", language, tier, null, null, page));
        return BaseSuccessResponse.of(GET_ROOM_LIST_SUCCESS, roomService.getSortedRoomList("item", language, tier, null, null, page));
    }

    // 게임방 정보 조회
    @GetMapping("/info")
    public BaseSuccessResponse<?> getInfo(@RequestParam("roomId") String roomId){
        return BaseSuccessResponse.of(GET_ROOM_INFO_SUCCESS, roomService.getRoomInfo(roomId));
    }
    // 게임에 참여한 유저 리스트 및 상태 반환
    @GetMapping("/userStatus")
    public BaseSuccessResponse<?> getUserStatus(@RequestParam("roomId") String roomId) {
        return BaseSuccessResponse.of(GET_USER_STATUS_SUCCESS, roomService.getUserStatus(roomId));
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
    public void sendMessageAll(@Payload ChatDto chat) {
        log.info("전체 채팅 : " + chat.getMessage());
        chat.setMessage(chat.getMessage());
        template.convertAndSend("/sub/chat/all", chat);
    }

    // 유저 퇴장 시에는 EventListener 을 통해서 유저 퇴장을 확인
    @EventListener
    public void webSocketDisconnectListener(SessionDisconnectEvent event) {
        log.info("DisConnEvent {}", event);
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // stomp 세션에 있던 uuid 와 roomId 를 확인해서 채팅방 유저 리스트와 room 에서 해당 유저를 삭제
        String userUUID = (String) headerAccessor.getSessionAttributes().get("userUUID");
        String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");

        log.info("headAccessor {}", headerAccessor);

        // 채팅방 유저 리스트에서 UUID 유저 닉네임 조회 및 리스트에서 유저 삭제

        String username = roomService.getUserName(roomId, userUUID);
        roomService.delUser(roomId, userUUID);

        if (username != null) {
            log.info("User Disconnected : " + username);

            // builder 어노테이션 활용
            ChatDto chat = ChatDto.builder()
                    .type(ChatDto.MessageType.LEAVE)
                    .sender(username)
                    .message(username + " 님 퇴장!!")
                    .build();

            template.convertAndSend("/sub/chat/room/" + roomId, chat);
            template.convertAndSend("/sub/normal/room-list", roomService.getSortedRoomList("normal",null, null, null, false, 1));
            template.convertAndSend("/sub/item/room-list", roomService.getSortedRoomList("item", null, null, null, null, 1));
            if(roomService.getUserStatus(roomId) != null) {
                template.convertAndSend("/sub/room/" + roomId + "/status", roomService.getUserStatus(roomId));
            }
        }
    }

    @PostMapping("/enter")
    public BaseSuccessResponse<?> putEnter(@RequestBody PostRoomEnterRequest request){
        return BaseSuccessResponse.of(POST_ENTER_ROOM_SUCCESS, roomService.enter(request));
    }

    // 방 입장처리
//    @MessageMapping("/room/enterUser")
//    public void enterUser(@Payload ChatDto chat, SimpMessageHeaderAccessor headerAccessor) {
//        // 채팅방에 유저 추가 및 UserUUID 반환
//        String userUUID = roomService.addUser(chat.getRoomId(), chat.getSender());
//
//        // 반환 결과를 socket session 에 userUUID 로 저장
//        headerAccessor.getSessionAttributes().put("userUUID", userUUID);
//        headerAccessor.getSessionAttributes().put("roomId", chat.getRoomId());
//
//
//        chat.setMessage(chat.getSender() + " 님 입장!!");
//        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);
//        template.convertAndSend("/sub/room/"+chat.getRoomId()+"/status", roomService.getUserStatus(chat.getRoomId()));
//
//        template.convertAndSend("/sub/normal/room-list", roomService.getSortedRoomList("normal",null, null, null, null, 1));
//        template.convertAndSend("/sub/item/room-list", roomService.getSortedRoomList("item", null, null, null, null,1));
//
//    }

    // 게임 준비
    @PutMapping("/ready")
    public BaseSuccessResponse<?> putReady(@RequestBody PutReadyRequest request){
        return BaseSuccessResponse.of(PUT_READY_SUCCESS, roomService.ready(request));
    }

    // 게임방 종료
    @DeleteMapping("")
    public BaseSuccessResponse<?> delete(@RequestBody DeleteRoomRequest request){
        return BaseSuccessResponse.of(DELETE_ROOM_SUCCESS, roomService.delete(request));
    }

    // 채팅방 비밀번호 체크
    @GetMapping("/password")
    public BaseSuccessResponse<?> getPassword(@RequestParam("roomId") String roomId, @RequestParam("roomPwd") String roomPwd){
        return BaseSuccessResponse.of(GET_ROOM_PASSWORD_CHECK_SUCCESS, roomService.checkPwd(roomId, roomPwd));
    }

    // 인원 수 체크
    @GetMapping("/personnel/check")
    public BaseSuccessResponse<?> getCheckPersonnel(@RequestParam("roomId") String roomId){
        return BaseSuccessResponse.of(GET_ROOM_PERSONNEL_CHECK_SUCCESS, roomService.checkPersonnel(roomId));
    }

    // 게임 시작
    @PutMapping("/start")
    public BaseSuccessResponse<?> putStart(@RequestParam("roomId") String roomId){
        return BaseSuccessResponse.of(PUT_ROOM_START_SUCCESS, roomService.checkReady(roomId));
    }

    @MessageMapping("/item/use")
    public void sendMessage(@Payload ItemDto itemDto) {
        log.info("공격 상황 : " + itemDto);
        template.convertAndSend("/sub/game/" + itemDto.getRoomId(), itemDto);
    }

    // 타이머 세팅
    @GetMapping("/set-timer")
    public BaseSuccessResponse<?> getSetTimer(@RequestParam("roomId") String roomId){
        return BaseSuccessResponse.of(GET_ROOM_TIMER_START_SUCCESS, roomService.startTimer(roomId));
    }
}