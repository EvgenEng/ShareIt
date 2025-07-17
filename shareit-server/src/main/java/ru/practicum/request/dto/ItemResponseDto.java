package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponseDto {
    private Long id;
    private String name;
    private Long ownerId;

    public ItemResponseDto(Long id, String name, String description, Boolean available, Long requestId) {
    }
}
