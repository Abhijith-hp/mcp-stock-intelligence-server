package com.gtl.MCPServer.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
public class HeartbeatController {
   

    @GetMapping("/heartbeat")
    public ResponseEntity<String> heartbeat() {
        log.info("Heartbeat endpoint was called");
        log.warn("Heartbeat warning log");
        log.error("Heartbeat error log");
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }
}
