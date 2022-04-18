package com.sykora.socialnetworkapp.config;

import com.sykora.socialnetworkapp.service.impl.JwtTokenImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import javax.servlet.ServletException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@SpringBootTest
public class JwtFilterTest {

    private MockHttpServletRequest mockRequest;
    private MockHttpServletResponse mockResponse;
    private MockFilterChain mockFilterChain;

    @Autowired
    private JwtTokenFilter filterToTest;

    @Autowired
    private JwtTokenImpl tokenService;

    @BeforeEach
    void setUp() throws Exception {
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
        mockFilterChain = new MockFilterChain();

        OAuth2User oAuth2User = createOAuth2User();
        Authentication authentication = getOauthAuthenticationFor(oAuth2User);

        String tokenValue = "Bearer " + tokenService.generateToken(authentication);
        mockRequest.addHeader("Authorization", tokenValue);

        SecurityContextHolder.clearContext();
    }

    @Test
    void testJwtFilter() throws ServletException, IOException {
        MockFilterChain mockFilterChainSpy = spy(mockFilterChain);
        filterToTest.doFilter(mockRequest, mockResponse, mockFilterChainSpy);

        verify(mockFilterChainSpy, times(1)).doFilter(mockRequest, mockResponse);
    }

    @Test
    void shouldReturnAuthenticationInSecurityContext() throws ServletException, IOException {
        filterToTest.doFilter(mockRequest, mockResponse, mockFilterChain);

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();

        assertThat(principal.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")), equalTo(true));
        assertThat(principal.getName(), equalTo("testUsername"));
        assertThat(principal.getAttribute("email"), equalTo("testEmail"));

    }

    @Test
    void shouldFilterContinuesToNextFilterWhenRequestHasNoToken() throws ServletException, IOException {
        MockFilterChain mockFilterChainSpy = spy(mockFilterChain);
        MockHttpServletRequest requestWithoutToken = new MockHttpServletRequest();

        filterToTest.doFilter(requestWithoutToken, mockResponse, mockFilterChainSpy);
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        verify(mockFilterChainSpy, times(1)).doFilter(requestWithoutToken, mockResponse);
        assertThat(authentication, equalTo(null));
    }

    private static OAuth2User createOAuth2User() {
        Map<String, Object> authorityAttributes = new HashMap<>();
        authorityAttributes.put("key", "value");

        GrantedAuthority authority = new OAuth2UserAuthority(authorityAttributes);

        Date issued = Date.from(LocalDateTime.now()
                .atZone(ZoneId.systemDefault())
                .toInstant());

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "test");
        attributes.put("id", "testId");
        attributes.put("name", "testUsername");
        attributes.put("email", "testEmail");
        attributes.put("iat", issued);

        return new DefaultOAuth2User(Collections.singletonList(authority), attributes, "sub");
    }

    private static Authentication getOauthAuthenticationFor(OAuth2User principal) {

        Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

        String authorizedClientRegistrationId = "my-oauth-client";

        return new OAuth2AuthenticationToken(principal, authorities, authorizedClientRegistrationId);
    }
}
