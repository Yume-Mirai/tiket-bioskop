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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserRepository usersRepository;
    private final UserRoleRepository userRoleRepository;
    private final JwtProvider jwtProvider;
    private final EmailService emailService;

    @Override
    public UserDetailDto login(LoginDto dto) {
        Optional<Users> optionalUsers = usersRepository.findByUsername(dto.getUsername());

        if (optionalUsers.isPresent()) {
            Users users = optionalUsers.get();

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
                        .id(users.getId())
                        .username(users.getUsername())
                        .email(users.getEmail())
                        .nomor(users.getNomor())
                        .tanggal_lahir(users.getTanggal_lahir())
                        .status(users.getStatus())
                        .createdDate(users.getCreatedDate())
                        .updateDate(users.getUpdateDate())
                        .role(String.join(",", roles))
                        .build();
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username atau Password Salah");
            }

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username atau Password Salah");
        }
    }

}
