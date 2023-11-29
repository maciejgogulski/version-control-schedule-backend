package com.maciejgogulski.eventschedulingbackend.service.impl;

import com.maciejgogulski.eventschedulingbackend.config.JwtService;
import com.maciejgogulski.eventschedulingbackend.config.UserDetailsImpl;
import com.maciejgogulski.eventschedulingbackend.domain.User;
import com.maciejgogulski.eventschedulingbackend.dto.AuthRequestDto;
import com.maciejgogulski.eventschedulingbackend.dto.AuthResponseDto;
import com.maciejgogulski.eventschedulingbackend.repositories.UserRepository;
import com.maciejgogulski.eventschedulingbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService, UserService {

    private static final Logger logger = LoggerFactory.getLogger(String.valueOf(UserDetailsImpl.class));

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("[loudUserByUsername] Username: " + username);
        Optional<User> user = userRepository.findByUsername(username);

        return user.map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika " + username));
    }


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
