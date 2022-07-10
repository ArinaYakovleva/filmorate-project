package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class ImMemoryBaseStorage<T extends Model> implements BaseStorage<T> {

    protected final Map<Long, T> dataHashMap = new HashMap<>();
    private Long generatorId = 0L;

    @Override
    public T add(T data) {
        dataHashMap.put(generatorId, data);
        log.info("Добавлен объект {}, {}", data.getClass(), data);
        return data;
    }

    @Override
    public T edit(T data) {
        T oldElement = dataHashMap.put(data.getId(), data);
        log.info("Объект изменен. Старое значение - {}. Новое значение - {}", oldElement, data);
        return data;
    }

    @Override
    public List<T> getAll() {
        return new ArrayList<>(dataHashMap.values());
    }

    @Override
    public T getOne(Long id) {
        return dataHashMap.get(id);
    }

    @Override
    public Long generateId() {
        return ++generatorId;
    }

}
