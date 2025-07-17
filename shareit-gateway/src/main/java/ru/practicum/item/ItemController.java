package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.util.HttpHeaders;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody ItemDto itemDto) {
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        return itemClient.update(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.getById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwner(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return itemClient.getAllByOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return itemClient.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addCommentToItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @Valid @RequestBody CommentDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }
}

/*package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.util.HttpHeaders;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/gateway/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(
            @RequestHeader(HttpHeaders.USER_ID_HEADER) long userId,
            @Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body((ItemDto) itemClient.create(userId, itemDto).getBody());
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(
            @RequestHeader(HttpHeaders.USER_ID_HEADER) long userId,
            @PathVariable long itemId,
            @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        return ResponseEntity.ok((ItemDto) itemClient.update(userId, itemDto).getBody());
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(
            @PathVariable long itemId,
            @RequestHeader(HttpHeaders.USER_ID_HEADER) long userId) {
        return ResponseEntity.ok((ItemDto) itemClient.getById(itemId, userId).getBody());
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItemsByOwner(
            @RequestHeader(HttpHeaders.USER_ID_HEADER) long ownerId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return ResponseEntity.ok((List<ItemDto>) itemClient.getAllByOwner(ownerId, from, size).getBody());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        if (text.isBlank()) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok((List<ItemDto>) itemClient.search(text, from, size).getBody());
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addCommentToItem(
            @RequestHeader(HttpHeaders.USER_ID_HEADER) long userId,
            @PathVariable long itemId,
            @Valid @RequestBody CommentDto commentDto) {
        return ResponseEntity.ok((CommentDto) itemClient.addComment(userId, itemId, commentDto).getBody());
    }
}
*/