package com.sykora.socialnetworkapp.service;

import org.springframework.security.core.Authentication;

public interface JwtTokenService {

    String generateToken(Authentication authentication) throws Exception;

    Authentication getAuth(String token) throws Exception;

}
