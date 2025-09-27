package com.uasjava.tiketbioskop.service;

import com.uasjava.tiketbioskop.dto.RegisterUserDto;
import com.uasjava.tiketbioskop.dto.UserDetailDto;
import org.springframework.data.domain.Page;

public interface UserService {
    void register(RegisterUserDto dto);
    Page<UserDetailDto> getAllUsers(int page, int size, String sortBy, String sortDir);
    Page<UserDetailDto> searchUsers(String keyword, int page, int size, String sortBy, String sortDir);
    Page<UserDetailDto> filterUsersByStatus(Boolean status, int page, int size, String sortBy, String sortDir);
}
