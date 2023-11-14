package ru.practicum.shareit;

public interface Mapper<E, D> {
    D toDto(E e);
    E toEntity(D d);
}
