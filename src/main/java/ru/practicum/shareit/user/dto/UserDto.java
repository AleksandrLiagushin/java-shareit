package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Builder
public class UserDto {
    private final long id;

    @NotNull
    @NotEmpty
    @NotBlank
    private final String name;

    @NotNull
    @NotEmpty
    @Email
    private final String email;
}
