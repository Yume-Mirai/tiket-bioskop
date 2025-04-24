package com.uasjava.tiketbioskop.service.impl;

import java.util.List;
import java.util.Optional;
// import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.uasjava.tiketbioskop.dto.LoginDto;
import com.uasjava.tiketbioskop.dto.UserDetailDto;
import com.uasjava.tiketbioskop.model.UserRole;
import com.uasjava.tiketbioskop.model.Users;
import com.uasjava.tiketbioskop.provider.JwtProvider;
import com.uasjava.tiketbioskop.repository.UserRepository;
import com.uasjava.tiketbioskop.repository.UserRoleRepository;
import com.uasjava.tiketbioskop.service.EmailService;
import com.uasjava.tiketbioskop.service.LoginService;
import com.uasjava.tiketbioskop.util.PasswordUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Autowired
    private final UserRepository usersRepository;
    @Autowired
    private final UserRoleRepository userRoleRepository;
    @Autowired
    private final JwtProvider jwtProvider;
    @Autowired
    private EmailService emailService;

    public LoginServiceImpl(UserRepository usersRepository, UserRoleRepository userRoleRepository,
            JwtProvider jwtProvider, EmailService emailService) {
        this.emailService = emailService;
        this.usersRepository = usersRepository;
        this.userRoleRepository = userRoleRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public UserDetailDto login(LoginDto dto) {
        // System.out.println(dto);
        Optional<Users> optionalUsers = usersRepository.findByUsername(dto.getUsername());

        if (optionalUsers.isPresent()) {
            Users users = optionalUsers.get();

            // ini untuk mengecek hashing password nya atau munculin hashing passwordnya di
            // terminalnya
            log.info(PasswordUtil.hash(dto.getPassword()));

            if (PasswordUtil.check(dto.getPassword(), users.getPassword())) {

                // Generic jwt token
                List<UserRole> userRoles = userRoleRepository.findByUsers(users);
                List<String> roles = userRoles.stream()
                        .map(userRole -> userRole.getRole().getRoleName())
                        .toList();

                String accessToken = jwtProvider.generateToken(users.getId(), users.getUsername(), roles);

                String subject = "Login Berhasil - Tiket Bioskop";
                String body = "Halo " + users.getUsername() + ",\n\n" +
                        "Kamu berhasil login ke sistem Tiket Bioskop.\n\n" +
                        "Berikut adalah token aksesmu (jangan dibagikan kepada siapa pun):\n\n" +
                        accessToken + "\n\n" +
                        "Salam,\nTiket Bioskop";
                emailService.sendEmail(users.getEmail(), subject, body);

                return UserDetailDto.builder()
                        .username(users.getUsername())
                        .role(String.join(",", roles))
                        .accessToken(accessToken)
                        .build();
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username atau Password Salah");
            }

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username atau Password Salah");
        }
    }

}
