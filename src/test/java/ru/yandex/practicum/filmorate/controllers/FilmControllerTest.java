package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;

import java.net.URI;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class FilmControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void validateFilmNameBlank() {
        Film film = new Film.FilmBuilder(null)
                .withName("")
                .withDescription("Описание фильма")
                .withReleaseDate(LocalDate.now())
                .withDuration(100)
                .build();

        RequestEntity<Film> requestEntity = new RequestEntity<>(film, HttpMethod.POST, URI.create("http://localhost:" + port + "/films"));
        ResponseEntity<Film> exchange = this.restTemplate.exchange(requestEntity, Film.class);
        assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void validateFilmDescription() {
        Film film = new Film.FilmBuilder(null)
                .withName("name")
                .withDescription("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.\n")
                .withReleaseDate(LocalDate.now())
                .withDuration(100)
                .build();

        RequestEntity<Film> requestEntity = new RequestEntity<>(film, HttpMethod.POST, URI.create("http://localhost:" + port + "/films"));
        ResponseEntity<Film> exchange = this.restTemplate.exchange(requestEntity, Film.class);
        assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void validateFilmReleaseDate() {
        Film film = new Film.FilmBuilder(null)
                .withName("name")
                .withDescription("Описание фильма")
                .withReleaseDate(LocalDate.parse("1865-12-25"))
                .withDuration(100)
                .build();

        RequestEntity<Film> requestEntity = new RequestEntity<>(film, HttpMethod.POST, URI.create("http://localhost:" + port + "/films"));
        ResponseEntity<Film> exchange = this.restTemplate.exchange(requestEntity, Film.class);
        assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void validateFilmNegativeDuration() {
        Film film = new Film.FilmBuilder(null)
                .withName("name")
                .withDescription("Описание фильма")
                .withReleaseDate(LocalDate.parse("1865-12-25"))
                .withDuration(-100)
                .build();
        RequestEntity<Film> requestEntity = new RequestEntity<>(film, HttpMethod.POST, URI.create("http://localhost:" + port + "/films"));
        ResponseEntity<Film> exchange = this.restTemplate.exchange(requestEntity, Film.class);
        assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

}