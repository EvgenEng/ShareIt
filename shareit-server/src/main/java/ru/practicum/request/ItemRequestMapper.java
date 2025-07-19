package ru.practicum.request;

import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemResponseDto;
import ru.practicum.user.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requestor) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }

    public static ItemRequestDto toDto(ItemRequest itemRequest, List<ItemResponseDto> items) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setCreated(itemRequest.getCreated());
        dto.setItems(items);
        return dto;
    }
}
