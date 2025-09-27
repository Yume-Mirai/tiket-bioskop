package com.uasjava.tiketbioskop.service.impl;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uasjava.tiketbioskop.dto.RegisterUserDto;
import com.uasjava.tiketbioskop.dto.UserDetailDto;
import com.uasjava.tiketbioskop.model.Role;
import com.uasjava.tiketbioskop.model.UserRole;
import com.uasjava.tiketbioskop.model.Users;
import com.uasjava.tiketbioskop.repository.RoleRepository;
import com.uasjava.tiketbioskop.repository.UserRepository;
import com.uasjava.tiketbioskop.repository.UserRoleRepository;
import com.uasjava.tiketbioskop.service.UserService;
import com.uasjava.tiketbioskop.util.PasswordUtil;
import org.springframework.data.domain.Page;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserRepository usersRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    UserServiceImpl(UserRepository usersRepository,
                    RoleRepository roleRepository,
                    UserRoleRepository userRoleRepository){

        this.usersRepository = usersRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }


    // register users
    @Transactional
    @Override
    public void register(RegisterUserDto dto){
        Users users = new Users();
        users.setId(dto.getId());
        users.setUsername(dto.getUsername());
        // users.setPassword(dto.getPassword());
        users.setPassword(PasswordUtil.hash(dto.getPassword())); // meng hashing password
        users.setEmail(dto.getEmail());
        users.setNomor(dto.getNomor());
        users.setTanggal_lahir(dto.getTanggal_lahir());
        users.setStatus(true);
        users.setCreatedDate(new Date());
        users.setUpdateDate(LocalDate.now());

        users = usersRepository.save(users);

        // Auto assign USER role to all registered users
        Role userRole = roleRepository.findByRoleName("USER");
        if (userRole != null) {
            UserRole userRoleEntity = new UserRole();
            userRoleEntity.setUsers(users);
            userRoleEntity.setRole(userRole);
            userRoleRepository.save(userRoleEntity);
            log.info("Auto assigned USER role to user: {}", users.getUsername());
        } else {
            log.warn("USER role not found, cannot assign role to user: {}", users.getUsername());
        }
    }

    @Override
    public Page<UserDetailDto> getAllUsers(int page, int size, String sortBy, String sortDir) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
            page, size,
            sortDir.equalsIgnoreCase("desc") ?
                org.springframework.data.domain.Sort.by(sortBy).descending() :
                org.springframework.data.domain.Sort.by(sortBy).ascending()
        );

        return usersRepository.findAll(pageable).map(user -> {
            UserDetailDto dto = new UserDetailDto();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setNomor(user.getNomor());
            dto.setTanggal_lahir(user.getTanggal_lahir());
            dto.setStatus(user.getStatus());
            dto.setCreatedDate(user.getCreatedDate());
            dto.setUpdateDate(user.getUpdateDate());
            return dto;
        });
    }

    @Override
    public Page<UserDetailDto> searchUsers(String keyword, int page, int size, String sortBy, String sortDir) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
            page, size,
            sortDir.equalsIgnoreCase("desc") ?
                org.springframework.data.domain.Sort.by(sortBy).descending() :
                org.springframework.data.domain.Sort.by(sortBy).ascending()
        );

        return usersRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            keyword, keyword, pageable).map(user -> {
            UserDetailDto dto = new UserDetailDto();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setNomor(user.getNomor());
            dto.setTanggal_lahir(user.getTanggal_lahir());
            dto.setStatus(user.getStatus());
            dto.setCreatedDate(user.getCreatedDate());
            dto.setUpdateDate(user.getUpdateDate());
            return dto;
        });
    }

    @Override
    public Page<UserDetailDto> filterUsersByStatus(Boolean status, int page, int size, String sortBy, String sortDir) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
            page, size,
            sortDir.equalsIgnoreCase("desc") ?
                org.springframework.data.domain.Sort.by(sortBy).descending() :
                org.springframework.data.domain.Sort.by(sortBy).ascending()
        );

        return usersRepository.findByStatus(status, pageable).map(user -> {
            UserDetailDto dto = new UserDetailDto();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setNomor(user.getNomor());
            dto.setTanggal_lahir(user.getTanggal_lahir());
            dto.setStatus(user.getStatus());
            dto.setCreatedDate(user.getCreatedDate());
            dto.setUpdateDate(user.getUpdateDate());
            return dto;
        });
    }
}
