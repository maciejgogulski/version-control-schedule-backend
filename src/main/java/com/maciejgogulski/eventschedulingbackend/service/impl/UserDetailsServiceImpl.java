package com.intrasoft.navigator2.backend.services.impl;

import com.intrasoft.navigator2.backend.config.UserDetailsImpl;
import com.intrasoft.navigator2.backend.domain.mysql.User;
import com.intrasoft.navigator2.backend.repositories.mysql.UserRepository;
import com.intrasoft.navigator2.backend.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Klasa implementująca interfejs z gotowego komponentu spring security do obsługi użytkowników.
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService, UserService {

    private static final Logger logger = LoggerFactory.getLogger(String.valueOf(UserDetailsImpl.class));

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("[loudUserByUsername] Username: " + username);
        Optional<User> user = userRepository.findByUsername(username);

        return user.map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika " + username));
    }

    public void addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
}
