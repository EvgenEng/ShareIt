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

/*package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.util.HttpHeaders;

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
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @Valid @RequestBody ItemDto itemDto) {
        log.info("Creating item for owner {}", ownerId);
        return itemService.create(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @PathVariable long itemId,
            @RequestBody ItemDto itemDto) {
        log.info("Updating item {} for owner {}", itemId, ownerId);
        itemDto.setId(itemId);
        return itemService.update(itemDto, ownerId);
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
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Getting all items for owner {}", ownerId);
        PageRequest page = PageRequest.of(from / size, size);
        return itemService.getAllByOwner(ownerId, page);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Searching items by text: {}", text);
        if (text.isBlank()) {
            return List.of();
        }
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
}


/*package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.util.HttpHeaders;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader(HttpHeaders.USER_ID_HEADER) long ownerId,
            @PathVariable long itemId,
            @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        return itemService.update(itemDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(
            @PathVariable long itemId,
            @RequestHeader(HttpHeaders.USER_ID_HEADER) long userId) {
        return itemService.getById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByOwner(
            @RequestHeader(HttpHeaders.USER_ID_HEADER) long ownerId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        PageRequest page = PageRequest.of(from / size, size);
        return itemService.getAllByOwner(ownerId, page);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        if (text.isBlank()) {
            return List.of();
        }
        PageRequest page = PageRequest.of(from / size, size);
        return itemService.search(text, page);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(
            @RequestHeader(HttpHeaders.USER_ID_HEADER) long userId,
            @PathVariable long itemId,
            @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
*/