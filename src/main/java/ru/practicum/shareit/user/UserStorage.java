package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    User create(User user);

    User update(User user);

    void delete(Long userId);

    User getById(Long userID);

    List<User> getAll();

    boolean existsById(long id);

    boolean existByEmail(String email);

    boolean contains(long userId);
}

