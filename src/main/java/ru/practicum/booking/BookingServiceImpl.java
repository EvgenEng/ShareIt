package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingResponseDto;
import ru.practicum.exception.*;
import ru.practicum.item.Item;
import ru.practicum.item.ItemRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingResponseDto createBooking(Long userId, BookingDto bookingDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getAvailable()) {
            throw new UnavailableItemException("Item is not available");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Owner cannot book own item");
        }

        Booking booking = bookingMapper.toEntity(bookingDto);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Booking.BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toResponseDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Only owner can approve booking");
        }
        if (!booking.getStatus().equals(Booking.BookingStatus.WAITING)) {
            throw new AlreadyProcessedException("Booking already processed");
        }

        booking.setStatus(approved ?
                Booking.BookingStatus.APPROVED :
                Booking.BookingStatus.REJECTED);

        Booking updatedBooking = bookingRepository.save(booking);
        return bookingMapper.toResponseDto(updatedBooking);
    }

    @Override
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Only owner or booker can view booking");
        }

        return bookingMapper.toResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getUserBookings(Long userId, String state, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        PageRequest page = PageRequest.of(from / size, size);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId, page);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, now, now, page);
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(
                        userId, now, page);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                        userId, now, page);
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        userId, Booking.BookingStatus.WAITING, page);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        userId, Booking.BookingStatus.REJECTED, page);
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(bookingMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(Long userId, String state, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        PageRequest page = PageRequest.of(from / size, size);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId, page);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, now, now, page);
                break;
            case "PAST":
                bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(
                        userId, now, page);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(
                        userId, now, page);
                break;
            case "WAITING":
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                        userId, Booking.BookingStatus.WAITING, page);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                        userId, Booking.BookingStatus.REJECTED, page);
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(bookingMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
