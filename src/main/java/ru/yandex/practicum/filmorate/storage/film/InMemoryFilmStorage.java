package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.ImMemoryBaseStorage;

import java.util.*;
import java.util.stream.Collectors;


@Component("filmStorageMemory")
@Slf4j
public class InMemoryFilmStorage extends ImMemoryBaseStorage<Film> implements FilmStorage {

    private final Map<Long, Set<Long>> likeHaspMap = new HashMap<>();
    private final Comparator<Film> filmComparator = Comparator.comparing(Film::getId, (s1, s2) -> {
        Integer sum1 = likeHaspMap.getOrDefault(s1, new HashSet<>()).size();
        Integer sum2 = likeHaspMap.getOrDefault(s2, new HashSet<>()).size();
        return sum2.compareTo(sum1);
    });

    @Override
    public void addLike(Long id, Long userId) {
        Set<Long> likeFilmSet = likeHaspMap.getOrDefault(id, new HashSet<>());
        likeFilmSet.add(userId);
        likeHaspMap.put(id, likeFilmSet);
        log.info("Добавлен лайк к фильму - {}", dataHashMap.get(id));
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        Set<Long> likeFilmSet = likeHaspMap.getOrDefault(id, new HashSet<>());
        likeFilmSet.remove(userId);
        likeHaspMap.put(id, likeFilmSet);
        log.info("Удален лайк к фильму - {}", dataHashMap.get(id));
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return dataHashMap.values()
                .stream()
                .sorted(filmComparator)
                .limit(count)
                .collect(Collectors.toList());
    }

}
