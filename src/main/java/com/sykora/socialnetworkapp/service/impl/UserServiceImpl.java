package com.sykora.socialnetworkapp.service.impl;

import com.sykora.socialnetworkapp.model.User;
import com.sykora.socialnetworkapp.repository.UserRepository;
import com.sykora.socialnetworkapp.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByGithubId(String id) {
        return userRepository.findByGithubId(id);
    }

    @Override
    public void postOauth2Login(String id) {
        User existsUser = userRepository.findByGithubId(id).orElse(null);
        if(existsUser == null){
            User newUser = new User();
            newUser.setGithubId(id);
            userRepository.save(newUser);
        }
    }
}
