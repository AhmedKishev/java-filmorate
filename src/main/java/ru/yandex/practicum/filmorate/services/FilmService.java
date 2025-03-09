package ru.yandex.practicum.filmorate.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFound;
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private Map<Long, List<Long>> likeForFilm = new HashMap<>();
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    private Optional<User> findUserForId(long idUser) {
        List<User> users = userStorage.getAllUsers();
        return users.stream()
                .filter(user -> user.getId() == idUser)
                .findFirst();
    }

    public void addLikeForFilm(long idFilm, long idUser) {
        Optional<User> findUser = findUserForId(idUser);
        if (findUser.isEmpty()) {
            throw new ObjectNotFound("Пользователь не найден");
        }
        if (likeForFilm.containsKey(idUser)) {
            if (likeForFilm.get(idUser).contains(idFilm)) {
                throw new RuntimeException("Пользователь с id " + idUser + " ставил лайк");
            }
        }
        Optional<Film> findFilm = filmStorage.getAllFilms().stream()
                .filter(film -> film.getId() == idFilm)
                .findFirst();
        if (findFilm.isPresent()) {
            findFilm.get().setLikes(findFilm.get().getLikes() + 1);
        } else throw new ObjectNotFound("Фильм не найден");
        if (likeForFilm.containsKey(idUser)) {
            likeForFilm.get(idUser).add(idFilm);
        }
    }

    public void deleteLikeFilm(long id, long userId) {
        Optional<User> findUser = findUserForId(userId);
        if (findUser.isEmpty()) {
            throw new ObjectNotFound("Пользователь не найден");
        }
        Optional<Film> findFilm = filmStorage.getAllFilms().stream()
                .filter(film -> film.getId() == id)
                .findFirst();
        if (findFilm.isPresent()) {
            findFilm.get().setLikes(findFilm.get().getLikes() - 1);
        } else throw new ObjectNotFound("Фильм не найден");
    }

    public List<Film> getFilmsForLike(int count) {
        Comparator<Film> comparatorForSort = new Comparator<Film>() {
            @Override
            public int compare(Film o1, Film o2) {
                return o2.getLikes() - o1.getLikes();
            }
        };
        List<Film> sortFilms = filmStorage.getAllFilms().stream()
                .sorted(comparatorForSort)
                .collect(Collectors.toList());
        if (count > filmStorage.getAllFilms().size()) {
            return sortFilms.subList(0, filmStorage.getAllFilms().size());
        } else return sortFilms.subList(0, count);
    }

}
