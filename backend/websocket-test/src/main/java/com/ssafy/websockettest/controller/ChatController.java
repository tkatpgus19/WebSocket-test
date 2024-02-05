package com.ssafy.websockettest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@Slf4j
@CrossOrigin("*")
public class ChatController {
    private final SimpMessageSendingOperations template;


}
