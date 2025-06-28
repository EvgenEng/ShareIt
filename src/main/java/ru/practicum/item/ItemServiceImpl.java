package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.Booking;
import ru.practicum.booking.BookingRepository;
import ru.practicum.exception.*;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = ItemMapper.toItem(itemDto, owner);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Long ownerId) {
        Item existingItem = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Only owner can update item");
        }

        ItemMapper.updateItemFromDto(itemDto, existingItem);
        Item updatedItem = itemRepository.save(existingItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getById(Long id, Long ownerId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        ItemDto itemDto = ItemMapper.toItemDto(item);

        if (item.getOwner().getId().equals(ownerId)) {
            LocalDateTime now = LocalDateTime.now();

            List<Booking> lastBookings = bookingRepository.findLastBooking(item.getId(), now);
            if (!lastBookings.isEmpty()) {
                itemDto.setLastBooking(new ItemDto.BookingShort(
                        lastBookings.get(0).getId(),
                        lastBookings.get(0).getBooker().getId()));
            }

            List<Booking> nextBookings = bookingRepository.findNextBooking(item.getId(), now);
            if (!nextBookings.isEmpty()) {
                itemDto.setNextBooking(new ItemDto.BookingShort(
                        nextBookings.get(0).getId(),
                        nextBookings.get(0).getBooker().getId()));
            }
        }

        itemDto.setComments(commentRepository.findByItemId(item.getId()).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList()));

        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllByOwner(Long ownerId) {
        return itemRepository.findByOwnerIdOrderById(ownerId).stream()
                .map(item -> {
                    ItemDto itemDto = ItemMapper.toItemDto(item);
                    LocalDateTime now = LocalDateTime.now();

                    List<Booking> lastBookings = bookingRepository.findLastBooking(item.getId(), now);
                    if (!lastBookings.isEmpty()) {
                        itemDto.setLastBooking(new ItemDto.BookingShort(
                                lastBookings.get(0).getId(),
                                lastBookings.get(0).getBooker().getId()));
                    }

                    List<Booking> nextBookings = bookingRepository.findNextBooking(item.getId(), now);
                    if (!nextBookings.isEmpty()) {
                        itemDto.setNextBooking(new ItemDto.BookingShort(
                                nextBookings.get(0).getId(),
                                nextBookings.get(0).getBooker().getId()));
                    }

                    itemDto.setComments(commentRepository.findByItemId(item.getId()).stream()
                            .map(CommentMapper::toDto)
                            .collect(Collectors.toList()));

                    return itemDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        List<Booking> bookings = bookingRepository.findCompletedBookings(
                itemId, userId, LocalDateTime.now());

        if (bookings.isEmpty()) {
            throw new InvalidCommentException("User never booked this item");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.toDto(savedComment);
    }
}
