package ru.practicum.item.dto;

import ru.practicum.booking.dto.BookingShortDto;
import lombok.Data;

import java.util.List;

@Data
public class ItemWithBookingDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private Long requestId;
    private List<CommentDto> comments;
}
