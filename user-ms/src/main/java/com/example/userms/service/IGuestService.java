package com.example.userms.service;

import com.example.userms.model.dto.response.AuthenticationResponse;

public interface IGuestService {

    AuthenticationResponse generateGuestToken();
}

