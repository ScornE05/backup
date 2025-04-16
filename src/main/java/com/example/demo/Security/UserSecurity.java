package com.example.demo.Security;

import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    public boolean isCurrentUser(Long userId, UserPrincipal userPrincipal) {
        return userPrincipal.getId().equals(userId);
    }
}
