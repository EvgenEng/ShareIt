package ru.practicum.item;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, Long ownerId);

    ItemDto update(ItemDto itemDto, Long ownerId);

    ItemDto getById(Long id, Long ownerId);

    List<ItemDto> getAllByOwner(Long ownerId, PageRequest pageRequest);

    List<ItemDto> search(String text, PageRequest pageRequest);

    @Transactional(readOnly = true)
    List<ItemDto> getAllByOwner(Long ownerId, Pageable pageable);

    @Transactional(readOnly = true)
    List<Object> search(String text, Pageable pageable);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
