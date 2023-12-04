package com.maciejgogulski.versioncontrolschedule.controllers;

import com.maciejgogulski.versioncontrolschedule.dto.AuthRequestDto;
import com.maciejgogulski.versioncontrolschedule.dto.AuthResponseDto;
import com.maciejgogulski.versioncontrolschedule.service.AuthService;
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

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto authRequest) {
        return ResponseEntity.ok(authService.login(authRequest));
    }
}
