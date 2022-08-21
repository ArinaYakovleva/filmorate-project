package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Slf4j
@Service
public class DirectorService extends BaseService<Director, DirectorStorage> {


    protected DirectorService(DirectorStorage storage) {
        super(storage);
    }

    @Override
    public Director edit(Director data) {
        try {
            return super.edit(data);
        } catch (Exception e) {
            String message = String.format("Не удалось обновить режиссера с id = %s", data.getId());
            log.warn(message, e);
            throw new NotFoundException(message, e);
        }
    }

    public List<Director> getAll() {
        return storage.getAll();
    }

    public Director getOne(Long id) {
        validateId(id);
        try {
            return storage.getOne(id);
        } catch (Exception e) {
            String message = String.format("Режиссер с id = %s не найден", id);
            log.warn(message, e);
            throw new NotFoundException(message, e);
        }
    }

    @Override
    protected void validate(Director data) {

    }

    public void remove(Long id) {
        try {
            storage.remove(id);
        } catch (Exception e) {
            String message = String.format("Не удалось удалить режиссера с id = %s", id);
            log.warn(message, e);
            throw new NotFoundException(message, e);
        }
    }
}
