package com.uasjava.tiketbioskop.service;

import com.uasjava.tiketbioskop.dto.LoginDto;
import com.uasjava.tiketbioskop.dto.UserDetailDto;

public interface LoginService {
    UserDetailDto login(LoginDto dto);
}
