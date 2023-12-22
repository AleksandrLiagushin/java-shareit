package ru.practicum.shareit.request;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class RequestDto {
    private long id;

    @NotNull
    @NotEmpty
    @Length(max = 1024)
    private String description;

    private long requester;
    private LocalDateTime created;
    private List<ItemsForRequestDto> items;
}
