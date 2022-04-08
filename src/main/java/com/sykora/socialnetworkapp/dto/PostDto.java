package com.sykora.socialnetworkapp.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
@Setter
public class PostDto {

    private Long id;

    @NotEmpty
    private String body;

    @NotEmpty
    private LocalDateTime createdAt;

    private UserDto user;
}
