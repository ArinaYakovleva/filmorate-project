package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friends {

    private Long userId;
    private Long friedId;
    private Boolean status;

}
