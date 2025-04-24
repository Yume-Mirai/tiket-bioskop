package com.uasjava.tiketbioskop.service.helper;

import com.uasjava.tiketbioskop.model.Users;
import com.uasjava.tiketbioskop.repository.UserRepository;
import com.uasjava.tiketbioskop.dto.UserCredentialsDto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthHelperService {

    private final UserRepository userRepository;

    public Users getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getCredentials() instanceof UserCredentialsDto)) {
            throw new RuntimeException("User is not authenticated");
        }

        UserCredentialsDto credentials = (UserCredentialsDto) auth.getCredentials();
        return userRepository.findByUsername(credentials.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
