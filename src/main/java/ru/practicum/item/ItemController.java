package ru.practicum.item;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.util.HttpHeaders;
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
    public ItemDto create(@RequestHeader(HttpHeaders.USER_ID_HEADER) Long ownerId,
                          @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(HttpHeaders.USER_ID_HEADER) Long ownerId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        return itemService.update(itemDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable Long itemId,
                           @RequestHeader(HttpHeaders.USER_ID_HEADER) Long ownerId) {
        return itemService.getById(itemId, ownerId);
    }

    @GetMapping
    public List<ItemDto> getAllByOwner(@RequestHeader(HttpHeaders.USER_ID_HEADER) Long ownerId) {
        return itemService.getAllByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return text.isBlank() ? List.of() : itemService.search(text);
    }
}
