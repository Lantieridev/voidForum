package com.voidforum.domain.port.in;

import com.voidforum.dto.UserLoginDto;
import com.voidforum.dto.UserRegisterDto;
import com.voidforum.dto.UserResponseDto;

import java.util.Map;

public interface AuthUseCase {
    UserResponseDto register(UserRegisterDto request);
    Map<String, Object> login(UserLoginDto request);
    Map<String, Object> getCurrentUser(String token);
}
