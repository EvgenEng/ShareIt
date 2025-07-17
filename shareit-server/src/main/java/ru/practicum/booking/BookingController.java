package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingResponseDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody BookingDto bookingDto) {
        log.info("Creating booking for user {}", userId);
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId,
            @RequestParam boolean approved) {
        log.info("Approving booking {} for user {}", bookingId, userId);
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId) {
        log.info("Getting booking {} for user {}", bookingId, userId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Getting bookings for user {} with state {}", userId, state);
        return bookingService.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Getting owner bookings for user {} with state {}", userId, state);
        return bookingService.getOwnerBookings(userId, state, from, size);
    }
}

/*package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingResponseDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingResponseDto create(
            @RequestHeader(USER_ID_HEADER) long userId,
            @Valid @RequestBody BookingDto bookingDto) {
        log.info("Creating booking for user {}", userId);
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(
            @RequestHeader(USER_ID_HEADER) long userId,
            @PathVariable long bookingId,
            @RequestParam boolean approved) {
        log.info("Approving booking {} for user {}", bookingId, userId);
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(
            @RequestHeader(USER_ID_HEADER) long userId,
            @PathVariable long bookingId) {
        log.info("Getting booking {} for user {}", bookingId, userId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getUserBookings(
            @RequestHeader(USER_ID_HEADER) long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Getting bookings for user {} with state {}", userId, state);
        return bookingService.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getOwnerBookings(
            @RequestHeader(USER_ID_HEADER) long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Getting owner bookings for user {} with state {}", userId, state);
        return bookingService.getOwnerBookings(userId, state, from, size);
    }
}

/*
package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingResponseDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingResponseDto create(
            @RequestHeader(USER_ID_HEADER) long userId,
            @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(
            @RequestHeader(USER_ID_HEADER) long userId,
            @PathVariable long bookingId,
            @RequestParam boolean approved) {
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(
            @RequestHeader(USER_ID_HEADER) long userId,
            @PathVariable long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getUserBookings(
            @RequestHeader(USER_ID_HEADER) long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return bookingService.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getOwnerBookings(
            @RequestHeader(USER_ID_HEADER) long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return bookingService.getOwnerBookings(userId, state, from, size);
    }
}
*/