package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class User extends Model {

    @Email(message = "Электронная почта должна содержать символ - @")
    private String email;
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата рождение не может быть из будущего")
    private LocalDate birthday;

    public User(Long id, @Email(message = "Электронная почта должна содержать символ - @") String email, @NotBlank(message = "Имя пользователя не может быть пустым") String login, String name, @PastOrPresent(message = "Дата рождение не может быть из будущего") LocalDate birthday) {
        super(id);
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
