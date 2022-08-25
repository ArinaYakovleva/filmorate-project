package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.storage.mpa.IMPAStorage;

import java.util.List;

@Service
public class MPAService extends ValidateService {

    private final IMPAStorage mpaStorage;

    @Autowired
    public MPAService(IMPAStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<MPARating> getAll() {
        return mpaStorage.getAll();
    }

    public MPARating getOne(Long id) {
        validateId(id);
        return mpaStorage.getOne(id);
    }

}
