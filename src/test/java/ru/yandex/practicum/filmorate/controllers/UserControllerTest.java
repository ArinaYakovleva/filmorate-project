package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.User;

import java.net.URI;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void validateUserEmail() {
        User user = new User(1L, "mail", "login", "name", LocalDate.now());
        RequestEntity<User> requestEntity = new RequestEntity<>(user, HttpMethod.POST, URI.create("http://localhost:" + port + "/users"));
        ResponseEntity<User> exchange = this.restTemplate.exchange(requestEntity, User.class);
        assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void validateUserLoginBlank() {
        User user = new User(1L, "mail@mail.ru", " ", "name", LocalDate.now());
        RequestEntity<User> requestEntity = new RequestEntity<>(user, HttpMethod.POST, URI.create("http://localhost:" + port + "/users"));
        ResponseEntity<User> exchange = this.restTemplate.exchange(requestEntity, User.class);
        assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void validateUserNameBlank() {
        User userNameBlank = new User(null, "mail@mail.ru", "dolore", " ", LocalDate.now());
        User userAssert = new User(1L, "mail@mail.ru", "dolore", "dolore", LocalDate.now());
        RequestEntity<User> requestEntity = new RequestEntity<>(userNameBlank, HttpMethod.POST, URI.create("http://localhost:" + port + "/users"));
        ResponseEntity<User> exchange = this.restTemplate.exchange(requestEntity, User.class);
        assertEquals(HttpStatus.OK, exchange.getStatusCode());
        assertEquals(userAssert, exchange.getBody());
    }

    @Test
    public void validateUserBirthdayTomorrow() {
        User user = new User(1L, "mail@mail.ru", " ", "name", LocalDate.now().plusDays(1));
        RequestEntity<User> requestEntity = new RequestEntity<>(user, HttpMethod.POST, URI.create("http://localhost:" + port + "/users"));
        ResponseEntity<User> exchange = this.restTemplate.exchange(requestEntity, User.class);
        assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }
}