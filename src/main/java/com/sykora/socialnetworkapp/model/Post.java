package com.sykora.socialnetworkapp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String body;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    private User user;

    public void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }
}
