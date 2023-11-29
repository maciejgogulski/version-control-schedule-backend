package com.intrasoft.navigator2.backend.services;

import com.intrasoft.navigator2.backend.domain.mysql.User;
import org.springframework.stereotype.Service;

/**
 * Interfejs do obsługi operacji na użytkownikach.
 */
@Service
public interface UserService {

    void addUser(User user);

}
