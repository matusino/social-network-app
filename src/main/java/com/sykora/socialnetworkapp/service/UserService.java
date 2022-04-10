package com.sykora.socialnetworkapp.service;

import com.sykora.socialnetworkapp.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {
    Optional<User> findByGithubId(String id);
    void postOauth2Login(String email);
}
