package com.application.saas.service;

import com.application.saas.dto.LoginRequest;
import com.application.saas.dto.LoginResponse;

public interface AuthenticationService {

    LoginResponse login(LoginRequest request);
}
