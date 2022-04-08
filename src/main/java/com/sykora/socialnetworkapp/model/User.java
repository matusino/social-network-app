package com.sykora.socialnetworkapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Please provide valid email address")
    @Email
    private String email;

    @NotEmpty
    @Column(unique=true)
    private String githubId;

    @Nullable
    @Column(name = "first_name")
    private String firstName;

    @Nullable
    @Column(name = "last_name")
    private String lastName;

    @Nullable
    private String gender;

    @Nullable
    private int age;

    @Nullable
    @Column(name = "birth_date")
    private LocalDate birthDate;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    // TODO: 3/29/2022 add friends logic
//    private List<User> friends;

}
