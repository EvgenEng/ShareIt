package ru.practicum.booking;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingResponseDto;
import java.util.List;

public interface BookingService {

    @Transactional
    BookingResponseDto createBooking(Long userId, BookingDto bookingDto);

    BookingResponseDto create(long userId, BookingDto bookingDto);

    BookingResponseDto approve(long userId, long bookingId, boolean approved);

    BookingResponseDto getById(long userId, long bookingId);

    List<BookingResponseDto> getUserBookings(long userId, String state, int from, int size);

    List<BookingResponseDto> getOwnerBookings(long userId, String state, int from, int size);

    @Transactional
    BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean approved);

    BookingResponseDto getBookingById(Long userId, Long bookingId);

    List<BookingResponseDto> getUserBookings(Long userId, String state, Integer from, Integer size);

    List<BookingResponseDto> getOwnerBookings(Long userId, String state, Integer from, Integer size);
}
