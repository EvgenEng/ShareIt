package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingResponseDto;
import ru.practicum.booking.handler.BookingStateHandler;
import ru.practicum.booking.handler.BookingStateHandlerChain;
import ru.practicum.exception.AlreadyProcessedException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.UnavailableItemException;
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
        private final BookingStateHandlerChain handlerChain;

        @Override
        @Transactional
        public BookingResponseDto create(long userId, BookingDto bookingDto) {
            User booker = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User not found"));
            Item item = itemRepository.findById(bookingDto.getItemId())
                    .orElseThrow(() -> new NotFoundException("Item not found"));

            validateBookingCreation(item, userId);

            Booking booking = bookingMapper.toEntity(bookingDto);
            booking.setItem(item);
            booking.setBooker(booker);
            booking.setStatus(Booking.BookingStatus.WAITING);

            Booking savedBooking = bookingRepository.save(booking);
            return bookingMapper.toResponseDto(savedBooking);
        }

        @Override
        @Transactional
        public BookingResponseDto approve(long userId, long bookingId, boolean approved) {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new NotFoundException("Booking not found"));

            validateBookingApproval(booking, userId);

            booking.setStatus(approved ?
                    Booking.BookingStatus.APPROVED :
                    Booking.BookingStatus.REJECTED);

            Booking updatedBooking = bookingRepository.save(booking);
            return bookingMapper.toResponseDto(updatedBooking);
        }

        @Override
        public BookingResponseDto getById(long userId, long bookingId) {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new NotFoundException("Booking not found"));

            if (!isUserOwnerOrBooker(booking, userId)) {
                throw new NotFoundException("Only owner or booker can view booking");
            }

            return bookingMapper.toResponseDto(booking);
        }

        @Override
        public List<BookingResponseDto> getUserBookings(long userId, String state, int from, int size) {
            validateUserExists(userId);
            PageRequest page = PageRequest.of(from / size, size);

            BookingStateHandler handler = handlerChain.getHandler(state);
            List<Booking> bookings = handler.handle(userId, page, LocalDateTime.now());

            return bookings.stream()
                    .map(bookingMapper::toResponseDto)
                    .collect(Collectors.toList());
        }

        @Override
        public List<BookingResponseDto> getOwnerBookings(long userId, String state, int from, int size) {
            validateUserExists(userId);
            PageRequest page = PageRequest.of(from / size, size);

            BookingStateHandler handler = handlerChain.getHandler("OWNER_" + state);
            List<Booking> bookings = handler.handle(userId, page, LocalDateTime.now());

            return bookings.stream()
                    .map(bookingMapper::toResponseDto)
                    .collect(Collectors.toList());
        }

    @Override
    @Transactional
    public BookingResponseDto createBooking(Long userId, BookingDto bookingDto) {
        return create(userId, bookingDto);
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        validateBookingApproval(booking, userId);

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

        if (!isUserOwnerOrBooker(booking, userId)) {
            throw new NotFoundException("Only owner or booker can view booking");
        }

        return bookingMapper.toResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getUserBookings(Long userId, String state, Integer from, Integer size) {
        return getUserBookingsObjParams(userId, state, from, size);
    }

    private List<BookingResponseDto> getUserBookingsObjParams(Long userId, String state, Integer from, Integer size) {
        validateUserExists(userId);
        PageRequest page = createPageRequest(from, size);

        BookingStateHandler handler = handlerChain.getHandler(state);
        List<Booking> bookings = handler.handle(userId, page, LocalDateTime.now());

        return mapToResponseDtoList(bookings);
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(Long userId, String state, Integer from, Integer size) {
        return getOwnerBookingsObjParams(userId, state, from, size);
    }

    private List<BookingResponseDto> getOwnerBookingsObjParams(Long userId, String state, Integer from, Integer size) {
        validateUserExists(userId);
        PageRequest page = createPageRequest(from, size);

        BookingStateHandler handler = handlerChain.getHandler("OWNER_" + state);
        List<Booking> bookings = handler.handle(userId, page, LocalDateTime.now());

        return mapToResponseDtoList(bookings);
    }

    private void validateBookingCreation(Item item, Long userId) {
        if (!item.getAvailable()) {
            throw new UnavailableItemException("Item is not available");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Owner cannot book own item");
        }
    }

    private void validateBookingApproval(Booking booking, Long userId) {
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Only owner can approve booking");
        }
        if (!booking.getStatus().equals(Booking.BookingStatus.WAITING)) {
            throw new AlreadyProcessedException("Booking already processed");
        }
    }

    private boolean isUserOwnerOrBooker(Booking booking, Long userId) {
        return booking.getBooker().getId().equals(userId) ||
                booking.getItem().getOwner().getId().equals(userId);
    }

    private void validateUserExists(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private PageRequest createPageRequest(Integer from, Integer size) {
        return PageRequest.of(from / size, size);
    }

    private List<BookingResponseDto> mapToResponseDtoList(List<Booking> bookings) {
        return bookings.stream()
                .map(bookingMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
