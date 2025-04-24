package com.uasjava.tiketbioskop.service.impl;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uasjava.tiketbioskop.dto.RegisterUserDto;
import com.uasjava.tiketbioskop.dto.RoleDto;
import com.uasjava.tiketbioskop.model.Role;
import com.uasjava.tiketbioskop.model.UserRole;
import com.uasjava.tiketbioskop.model.Users;
import com.uasjava.tiketbioskop.repository.RoleRepository;
import com.uasjava.tiketbioskop.repository.UserRepository;
import com.uasjava.tiketbioskop.repository.UserRoleRepository;
import com.uasjava.tiketbioskop.service.UserService;
import com.uasjava.tiketbioskop.util.PasswordUtil;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private final UserRepository usersRepository;
    @Autowired
    private final RoleRepository roleRepository;
    @Autowired
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
        users.setCreatedDate(java.sql.Date.valueOf(LocalDate.now()));
        users.setUpdateDate(LocalDate.now());

        users = usersRepository.save(users);

        for(RoleDto roleDto : dto.getRoles()){
            Role role = roleRepository.findById(roleDto.getId()).orElse(null);

            UserRole userRole = new UserRole();
            userRole.setUsers(users);
            userRole.setRole(role);

            userRoleRepository.save(userRole);
        }
    }



    
}
