package dev.hsu.potatotest.controller;

import org.springframework.http.ResponseEntity;

public class BaseController {

    public ResponseEntity makeResponse() {
        return ResponseEntity.ok().build();
    }

    public ResponseEntity makeResponse(int status) {
        return ResponseEntity.status(status).build();
    }


}
