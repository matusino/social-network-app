package com.sykora.socialnetworkapp.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JwtTokenServiceImplTest {

    @Mock
    private JwtTokenImpl tokenService;

    @Mock
    private Authentication authentication;

    @Test
    public void shouldGenerateAuthToken() throws Exception {
        given(tokenService.generateToken(any(Authentication.class))).willReturn("testToken");
        String token = tokenService.generateToken(authentication);

        then(tokenService).should().generateToken(authentication);
        assertThat(token).isEqualTo("testToken");
        verify(tokenService, times(1)).generateToken(authentication);
    }

    @Test
    public void shouldReturnAuthFromToken() throws Exception {
        given(tokenService.getAuth(any(String.class))).willReturn(authentication);

        Authentication auth = tokenService.getAuth("token");

        then(tokenService).should().getAuth("token");
        assertThat(auth).isEqualTo(authentication);
        verify(tokenService, times(1)).getAuth("token");

    }

}