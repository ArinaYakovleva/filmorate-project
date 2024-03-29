package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.IGenreStorage;

import java.util.List;

@Service
public class GenreService extends ValidateService {

    private final IGenreStorage genreStorage;

    @Autowired
    public GenreService(IGenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> getAll() {
        return genreStorage.getAll();
    }

    public Genre getOne(Long id) {
        validateId(id);
        return genreStorage.getOne(id);
    }

}
