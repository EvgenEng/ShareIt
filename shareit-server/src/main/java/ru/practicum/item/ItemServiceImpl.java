package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.Booking;
import ru.practicum.booking.BookingRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Item name cannot be empty");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Item description cannot be empty");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Available status must be specified");
        }

        Item item = itemMapper.toItem(itemDto, owner);
        Item savedItem = itemRepository.save(item);
        return itemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item existingItem = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Only owner can update item");
        }

        itemMapper.updateItemFromDto(itemDto, existingItem);
        Item updatedItem = itemRepository.save(existingItem);
        return enrichAndConvertToDto(updatedItem, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getById(Long itemId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        ItemDto itemDto = itemMapper.toItemDto(item);

        if (item.getOwner().getId().equals(userId)) {
            addBookingInfo(itemDto, item.getId());
        }

        addCommentsInfo(itemDto, item.getId());
        return itemDto;
    }

    @Override
    public List<ItemDto> getAllByOwner(Long ownerId, PageRequest pageRequest) {
        return itemRepository.findByOwnerIdOrderById(ownerId, pageRequest).stream()
                .map(item -> enrichAndConvertToDto(item, ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text, PageRequest pageRequest) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text, pageRequest).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllByOwner(Long ownerId, Pageable pageable) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return itemRepository.findByOwnerIdOrderById(ownerId, pageable).stream()
                .map(item -> enrichAndConvertToDto(item, ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Object> search(String text, Pageable pageable) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text, pageable).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            throw new ValidationException("Comment text cannot be empty");
        }

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        validateUserBookedItem(itemId, userId);

        Comment comment = commentMapper.toEntity(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    private ItemDto enrichAndConvertToDto(Item item, Long userId) {
        ItemDto itemDto = itemMapper.toItemDto(item);
        if (item.getOwner().getId().equals(userId)) {
            addBookingInfo(itemDto, item.getId());
        }
        addCommentsInfo(itemDto, item.getId());
        return itemDto;
    }

    private void addBookingInfo(ItemDto itemDto, Long itemId) {
        LocalDateTime now = LocalDateTime.now();

        bookingRepository.findLastBooking(itemId, now)
                .stream()
                .findFirst()
                .ifPresent(booking -> {
                    ItemDto.BookingShort bookingShort = new ItemDto.BookingShort();
                    bookingShort.setId(booking.getId());
                    bookingShort.setBookerId(booking.getBooker().getId());
                    itemDto.setLastBooking(bookingShort);
                });

        bookingRepository.findNextBooking(itemId, now)
                .stream()
                .findFirst()
                .ifPresent(booking -> {
                    ItemDto.BookingShort bookingShort = new ItemDto.BookingShort();
                    bookingShort.setId(booking.getId());
                    bookingShort.setBookerId(booking.getBooker().getId());
                    itemDto.setNextBooking(bookingShort);
                });
    }

    private void addCommentsInfo(ItemDto itemDto, Long itemId) {
        List<CommentDto> comments = commentRepository.findByItemId(itemId)
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
        itemDto.setComments(comments);
    }

    private void validateUserBookedItem(Long itemId, Long userId) {
        List<Booking> bookings = bookingRepository.findCompletedBookings(
                itemId, userId, LocalDateTime.now());

        if (bookings.isEmpty()) {
            throw new ValidationException("User never booked this item");
        }
    }
}
