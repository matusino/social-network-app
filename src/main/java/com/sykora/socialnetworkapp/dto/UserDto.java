package com.sykora.socialnetworkapp.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserDto {

    private Long id;

    @NotEmpty(message = "Please provide valid email address")
    @Email
    private String email;

    @NotEmpty
    @Column(unique=true)
    private String githubId;

    @Nullable
    private String firstName;

    @Nullable
    private String lastName;

    @Nullable
    private String gender;

    @Nullable
    private int age;

    @Nullable
    private LocalDate birthDate;

    private List<PostDto> posts = new ArrayList<>();

}
