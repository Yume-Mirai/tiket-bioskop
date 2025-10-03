package com.uasjava.tiketbioskop.service;

import com.uasjava.tiketbioskop.dto.LoginDto;
import com.uasjava.tiketbioskop.dto.LoginResponseDto;

public interface LoginService {
    LoginResponseDto login(LoginDto dto);
}
