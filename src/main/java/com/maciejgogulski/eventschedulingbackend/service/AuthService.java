package com.maciejgogulski.eventschedulingbackend.service;

import com.maciejgogulski.eventschedulingbackend.dto.AuthRequestDto;
import com.maciejgogulski.eventschedulingbackend.dto.AuthResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    AuthResponseDto login(AuthRequestDto request);
}
