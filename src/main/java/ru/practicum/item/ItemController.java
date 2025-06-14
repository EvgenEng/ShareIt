package ru.practicum.item;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @Valid @RequestBody ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Поле available обязательно");
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Имя не может быть пустым");
        }
        return itemService.create(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        return itemService.update(itemDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable Long itemId,
                           @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getById(itemId, ownerId);
    }

    @GetMapping
    public List<ItemDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getAllByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemService.search(text);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(NotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationException(ValidationException e) {
        return e.getMessage();
    }
}
