package com.maciejgogulski.versioncontrolschedule.service.impl;

import com.maciejgogulski.versioncontrolschedule.config.JwtService;
import com.maciejgogulski.versioncontrolschedule.dto.AuthRequestDto;
import com.maciejgogulski.versioncontrolschedule.dto.AuthResponseDto;
import com.maciejgogulski.versioncontrolschedule.repositories.UserRepository;
import com.maciejgogulski.versioncontrolschedule.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;


    @Override
    public AuthResponseDto login(AuthRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

        var jwt = jwtService.generateJwt(user);

        return AuthResponseDto.builder()
                .token(jwt)
                .build();
    }
}
