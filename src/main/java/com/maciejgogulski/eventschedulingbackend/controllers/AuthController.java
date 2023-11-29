package com.maciejgogulski.eventschedulingbackend.controllers;

import com.maciejgogulski.eventschedulingbackend.dto.AuthRequestDto;
import com.maciejgogulski.eventschedulingbackend.dto.AuthResponseDto;
import com.maciejgogulski.eventschedulingbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto authRequest) {
        return ResponseEntity.ok(userService.login(authRequest));
    }
}
