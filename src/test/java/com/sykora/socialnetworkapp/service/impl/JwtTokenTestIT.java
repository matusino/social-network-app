package com.sykora.socialnetworkapp.service.impl;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class JwtTokenTestIT {

    @Autowired
    private JwtTokenImpl tokenService;

    private Authentication authentication;

    @BeforeEach
    void setUp() {
        OAuth2User oAuth2User = createOAuth2User();
        authentication = getOauthAuthenticationFor(oAuth2User);
    }

    @Test
    public void shouldGenerateAuthToken() throws Exception {
        String token = tokenService.generateToken(this.authentication);

        assertThat(token).isNotNull();
    }

    @Test
    public void expiredTokenShouldThrowRuntimeException(){
        //given
        String expiredToken = "eyJraWQiOiJjNWQ0ZDcwNDE5YmQ0OTA5YTFlNTAyODEyYzZlMWYyYiIsInR5cCI6IkpXVCIsImFsZyI6IkVTMjU2In0.eyJzdWIiOiJ0ZXN0SWQiLCJjbGllbnRSZWdpc3RyYXRpb25JZCI6Im15LW9hdXRoLWNsaWVudCIsImF0dHJpYnV0ZXMiOnsic3ViIjoidGVzdCIsIm5hbWUiOiJ0ZXN0IiwiaWQiOiJ0ZXN0SWQiLCJlbWFpbCI6InRlc3QifSwiZXhwIjoxNjUwMTUzMTUxLCJuYW1lZEF0dHJpYnV0ZUtleSI6Im5hbWUiLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXX0.SxV5jSmu9G2DAEgMYuDQe7JoW9gWKU6UBY9vkYqp-aQhDRA3hPKaWYyHEO3aS-UIRO5lctrPBUPaCTkBWTk3pA";

        assertThatThrownBy(() -> {
            tokenService.getAuth(expiredToken);
        }).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Token Expired!!");
    }

    @Test
    void testTokenExpirationOf5Hours() throws Exception {
        Date expiration = getClaimsSet(authentication).getExpirationTime();

        Date date = Date.from(LocalDateTime.now()
                .plus(6, ChronoUnit.HOURS)
                .atZone(ZoneId.systemDefault())
                .toInstant());

        long diffInMillies = Math.abs(expiration.getTime() - date.getTime());
        long remain = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        assertThat(expiration).isBefore(date);
        assertThat(remain).isEqualTo(1L);
    }

    @Test
    void shouldParseClientRegistrationIdFromJwt() throws Exception {
        String clientRegistrationId = (String) getClaimsSet(authentication).getClaim("clientRegistrationId");

        assertThat(clientRegistrationId).isNotNull();
        assertThat(clientRegistrationId).isEqualTo("my-oauth");
    }

    @Test
    void shouldParseAttributesFromJwt() throws Exception {
        Map<String, Object> attributes = (Map<String, Object>)getClaimsSet(authentication).getClaim("attributes");
        assertThat(attributes, IsMapContaining.hasEntry("sub", "test") );
        assertThat(attributes, IsMapContaining.hasEntry("id", "testId") );
        assertThat(attributes, IsMapContaining.hasEntry("name", "test") );
        assertThat(attributes, IsMapContaining.hasEntry("email", "test") );
    }

    @Test
    void shouldParseAuthoritiesFromJwt() throws Exception {
        Collection<? extends GrantedAuthority> authorities =((List<String>) getClaimsSet(authentication).getClaim("authorities"))
                .stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());

        assertThat(authorities.contains("ROLE_USER"));
    }

    private JWTClaimsSet getClaimsSet(Authentication authentication) throws Exception {
        String accessToken = tokenService.generateToken(authentication);
        SignedJWT signedJWT = SignedJWT.parse(accessToken);

        return signedJWT.getJWTClaimsSet();
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
        attributes.put("name", "test");
        attributes.put("email", "test");
        attributes.put("iat", issued);

        return new DefaultOAuth2User(Collections.singletonList(authority), attributes, "sub");
    }

    private static Authentication getOauthAuthenticationFor(OAuth2User principal) {

        Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

        String authorizedClientRegistrationId = "my-oauth";

        return new OAuth2AuthenticationToken(principal, authorities, authorizedClientRegistrationId);
    }

}
