package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody ItemDto itemDto) {
        log.info("Creating item for user {}", userId);
        return itemService.create(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting item {} for user {}", itemId, userId);
        return itemService.getById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByOwner(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Getting all items for owner {}", userId);
        PageRequest page = PageRequest.of(from / size, size);
        return itemService.getAllByOwner(userId, page);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Searching items by text: {}", text);
        PageRequest page = PageRequest.of(from / size, size);
        return itemService.search(text, page);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @Valid @RequestBody CommentDto commentDto) {
        log.info("Adding comment to item {} by user {}", itemId, userId);
        return itemService.addComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @RequestBody ItemDto itemDto) {
        log.info("Updating item {} for user {}", itemId, userId);
        itemDto.setId(itemId);
        return itemService.update(itemDto, userId);
    }
}
