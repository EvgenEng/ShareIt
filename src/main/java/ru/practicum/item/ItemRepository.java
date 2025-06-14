package ru.practicum.item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item save(Item item);

    Optional<Item> findById(Long id);

    List<Item> findAllByOwnerId(Long ownerId);

    List<Item> search(String text);

    List<Item> findByRequestId(Long requestId);
}
