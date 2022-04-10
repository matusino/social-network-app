package com.sykora.socialnetworkapp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    @GetMapping("/user")
    public Map<String, String> getUserName(@AuthenticationPrincipal( expression = "attributes['name']" ) String username) {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        Object principal = authentication.getPrincipal();
        System.out.println(principal);
        Map<String,Object> attributes = new HashMap<>();
        if(principal instanceof DefaultOAuth2User){
            attributes =  ((DefaultOAuth2User) principal).getAttributes(); //get user attributes from github
        }
        return Collections.singletonMap("name", username);

    }

}
