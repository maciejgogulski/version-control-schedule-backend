package com.maciejgogulski.versioncontrolschedule.service;

import com.maciejgogulski.versioncontrolschedule.dto.AuthRequestDto;
import com.maciejgogulski.versioncontrolschedule.dto.AuthResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    AuthResponseDto login(AuthRequestDto request);
}
