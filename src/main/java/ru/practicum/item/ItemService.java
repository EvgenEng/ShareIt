package ru.practicum.item;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, Long ownerId);

    ItemDto update(ItemDto itemDto, Long ownerId);

    ItemDto getById(Long id, Long ownerId);

    List<ItemDto> getAllByOwner(Long ownerId);

    List<ItemDto> search(String text);
}
