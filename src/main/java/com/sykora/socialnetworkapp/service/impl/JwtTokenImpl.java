package com.sykora.socialnetworkapp.service.impl;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sykora.socialnetworkapp.service.JwtTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

import java.security.interfaces.ECPrivateKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenImpl implements JwtTokenService {

    @Value("${jwt.token.sign.key}")
    private String jwtTokenSignKey;

    private final String REGISTRATION_ID = "clientRegistrationId";
    private final String NAMED_KEY = "namedAttributeKey";
    private final String AUTHORITIES = "authorities";
    private final String ATTRIBUTES = "attributes";

    public String generateToken(Authentication authentication) throws Exception {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        DefaultOAuth2User userDetails = (DefaultOAuth2User) token.getPrincipal();

        List<String> auth = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(Objects.requireNonNull(userDetails.getAttribute("id")).toString())
                .expirationTime(getDate(5, ChronoUnit.HOURS))
                .claim(NAMED_KEY,"name")
                .claim(ATTRIBUTES, userDetails.getAttributes())
                .claim(AUTHORITIES, auth)
                .claim(REGISTRATION_ID, token.getAuthorizedClientRegistrationId())
                .build();

        ECKey key = new ECKeyGenerator(Curve.P_256).keyID(jwtTokenSignKey).generate();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                .type(JOSEObjectType.JWT)
                .keyID(key.getKeyID())
                .build();

        SignedJWT jwt = new SignedJWT(header, claimsSet);
        jwt.sign(new ECDSASigner((ECPrivateKey) key.toPrivateKey()));

        return jwt.serialize();
    }

    public Authentication getAuth(String jwt) throws Exception {
        SignedJWT signedJWT = SignedJWT.parse(jwt);

        validateJwt(signedJWT);

        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

        String clientRegistrationId = (String) claimsSet.getClaim(REGISTRATION_ID);
        String namedAttributeKey = (String) claimsSet.getClaim(NAMED_KEY);
        Map<String, Object> attributes = (Map<String, Object>)claimsSet.getClaim(ATTRIBUTES);
        Collection<? extends GrantedAuthority> authorities =((List<String>) claimsSet.getClaim(AUTHORITIES))
                .stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());

        return new OAuth2AuthenticationToken(
                new DefaultOAuth2User(authorities, attributes, namedAttributeKey),
                authorities,
                clientRegistrationId
        );
    }

    private static Date getDate(long amount, TemporalUnit unit) {
        return Date.from(LocalDateTime.now()
                        .plus(amount, unit)
                        .atZone(ZoneId.systemDefault())
                        .toInstant());
    }

    private void validateJwt(JWT jwt) throws Exception {
        if(jwt.getJWTClaimsSet().getExpirationTime().before(new Date())){
            throw new RuntimeException("Token Expired!!");
        }
    }

}
