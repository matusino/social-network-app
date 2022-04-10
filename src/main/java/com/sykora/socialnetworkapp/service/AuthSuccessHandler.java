package com.sykora.socialnetworkapp.service;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenService jwtProvider;
    private final ObjectMapper mapper;
    private final UserService userService;

    public AuthSuccessHandler(JwtTokenService jwtProvider, ObjectMapper mapper, UserService userService) {
        this.jwtProvider = jwtProvider;
        this.mapper = mapper;
        this.userService = userService;
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException  {
        String token;
        Object principal = authentication.getPrincipal();
        Map<String,Object> attributes;
        attributes =  ((DefaultOAuth2User) principal).getAttributes();
        userService.postOauth2Login(attributes.get("id").toString());
        try {
            token = jwtProvider.generateToken(authentication);
            response.getWriter().write(
                    mapper.writeValueAsString(Collections.singletonMap("accessToken", token))
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
