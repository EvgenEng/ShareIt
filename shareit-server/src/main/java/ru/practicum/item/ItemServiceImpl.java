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

/*package ru.practicum.item;

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
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        try {
            log.info("Creating item for owner {}", ownerId);

            User owner = userRepository.findById(ownerId)
                    .orElseThrow(() -> {
                        log.error("User with id {} not found", ownerId);
                        return new NotFoundException("User not found");
                    });

            // Валидация уже выполняется через аннотации в ItemDto
            // Но добавим дополнительные проверки для безопасности
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
            log.info("Successfully created item with id {}", savedItem.getId());

            return convertToDto(savedItem);

        } catch (Exception e) {
            log.error("Error creating item", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Long ownerId) {
        log.info("Updating item {} for owner {}", itemDto.getId(), ownerId);
        Item existingItem = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> {
                    log.error("Item with id {} not found", itemDto.getId());
                    return new NotFoundException("Item not found");
                });

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            log.error("User {} is not owner of item {}", ownerId, itemDto.getId());
            throw new NotFoundException("Only owner can update item");
        }

        itemMapper.updateItemFromDto(itemDto, existingItem);
        Item updatedItem = itemRepository.save(existingItem);
        log.debug("Updated item with id {}", updatedItem.getId());

        return enrichAndConvertToDto(updatedItem, ownerId);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getById(Long itemId, Long userId) {
        log.info("Getting item {} for user {}", itemId, userId);

        if (!userRepository.existsById(userId)) {
            log.error("User with id {} not found", userId);
            throw new NotFoundException("User not found");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Item with id {} not found", itemId);
                    return new NotFoundException("Item not found");
                });

        ItemDto itemDto = convertToDto(item);

        if (item.getOwner().getId().equals(userId)) {
            addBookingInfo(itemDto, item.getId());
        }

        addCommentsInfo(itemDto, item.getId());

        return itemDto;
    }

    @Override
    public List<ItemDto> getAllByOwner(Long ownerId, PageRequest pageRequest) {
        log.info("Getting all items for owner {}", ownerId);
        return itemRepository.findByOwnerIdOrderById(ownerId, pageRequest).stream()
                .map(item -> enrichAndConvertToDto(item, ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text, PageRequest pageRequest) {
        log.info("Searching items by text: {}", text);
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text, pageRequest).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllByOwner(Long ownerId, Pageable pageable) {
        // Проверяем существование пользователя
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("User not found");
        }

        // Получаем страницу предметов владельца
        List<Item> items = itemRepository.findByOwnerIdOrderById(ownerId, pageable);

        // Преобразуем каждый Item в ItemDto с дополнительной информацией
        return items.stream()
                .map(item -> {
                    ItemDto dto = itemMapper.toItemDto(item);
                    // Добавляем информацию о бронированиях только для владельца
                    if (item.getOwner().getId().equals(ownerId)) {
                        addBookingInfo(dto, item.getId());
                    }
                    // Добавляем комментарии
                    addCommentsInfo(dto, item.getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Object> search(String text, Pageable pageable) {
        // Если текст пустой - возвращаем пустой список
        if (text == null || text.isBlank()) {
            return List.of();
        }

        // Ищем доступные предметы по тексту
        List<Item> items = itemRepository.search(text.toLowerCase(), pageable);

        // Преобразуем в DTO (без информации о бронированиях)
        return items.stream()
                .map(item -> {
                    ItemDto dto = itemMapper.toItemDto(item);
                    // Добавляем только комментарии
                    addCommentsInfo(dto, item.getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        log.info("Adding comment to item {} by user {}", itemId, userId);
        User author = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with id {} not found", userId);
                    return new NotFoundException("User not found");
                });

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Item with id {} not found", itemId);
                    return new NotFoundException("Item not found");
                });

        validateUserBookedItem(itemId, userId);

        Comment comment = commentMapper.toEntity(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        log.debug("Added comment with id {}", savedComment.getId());

        return commentMapper.toDto(savedComment);
    }

    private ItemDto convertToDto(Item item) {
        ItemDto dto = itemMapper.toItemDto(item);
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setRequestId(item.getRequestId());
        return dto;
    }

    private void addBookingInfo(ItemDto itemDto, Long itemId) {
        try {
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
        } catch (Exception e) {
            log.error("Error adding booking info for item {}", itemId, e);
        }
    }

    private void addCommentsInfo(ItemDto itemDto, Long itemId) {
        try {
            List<CommentDto> comments = commentRepository.findByItemId(itemId)
                    .stream()
                    .map(commentMapper::toDto)
                    .collect(Collectors.toList());
            itemDto.setComments(comments);
        } catch (Exception e) {
            log.error("Error adding comments info for item {}", itemId, e);
            itemDto.setComments(Collections.emptyList());
        }
    }

    private ItemDto enrichAndConvertToDto(Item item, Long ownerId) {
        ItemDto itemDto = convertToDto(item);
        if (item.getOwner().getId().equals(ownerId)) {
            addBookingInfo(itemDto, item.getId());
        }
        addCommentsInfo(itemDto, item.getId());
        return itemDto;
    }

    private void validateUserBookedItem(Long itemId, Long userId) {
        List<Booking> bookings = bookingRepository.findCompletedBookings(
                itemId, userId, LocalDateTime.now());

        if (bookings.isEmpty()) {
            log.error("User {} never booked item {}", userId, itemId);
            throw new ValidationException("User never booked this item");
        }
    }
}

/*package ru.practicum.item;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.Booking;
import ru.practicum.booking.BookingRepository;
import ru.practicum.exception.InvalidCommentException;
import ru.practicum.exception.NotFoundException;
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
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        try {
            log.info("Creating item for owner {}", ownerId);

            User owner = userRepository.findById(ownerId)
                    .orElseThrow(() -> {
                        log.error("User with id {} not found", ownerId);
                        return new NotFoundException("User not found");
                    });

            if (itemDto.getName() == null || itemDto.getName().isBlank()) {
                throw new ValidationException("Item name cannot be empty");
            }
            if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
                throw new ValidationException("Item description cannot be empty");
            }
            if (itemDto.getAvailable() == null) {
                throw new ValidationException("Available status must be specified");
            }

            Item item = new Item();
            item.setName(itemDto.getName());
            item.setDescription(itemDto.getDescription());
            item.setAvailable(itemDto.getAvailable());
            item.setOwner(owner);

            if (itemDto.getRequestId() != null) {
                item.setRequestId(itemDto.getRequestId());
            }

            Item savedItem = itemRepository.save(item);
            log.info("Successfully created item with id {}", savedItem.getId());

            ItemDto responseDto = new ItemDto();
            responseDto.setId(savedItem.getId());
            responseDto.setName(savedItem.getName());
            responseDto.setDescription(savedItem.getDescription());
            responseDto.setAvailable(savedItem.getAvailable());
            responseDto.setRequestId(savedItem.getRequestId());

            return responseDto;

        } catch (Exception e) {
            log.error("Error creating item", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Long ownerId) {
        log.info("Updating item {} for owner {}", itemDto.getId(), ownerId);
        Item existingItem = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> {
                    log.error("Item with id {} not found", itemDto.getId());
                    return new NotFoundException("Item not found");
                });

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            log.error("User {} is not owner of item {}", ownerId, itemDto.getId());
            throw new NotFoundException("Only owner can update item");
        }

        itemMapper.updateItemFromDto(itemDto, existingItem);
        Item updatedItem = itemRepository.save(existingItem);
        log.debug("Updated item with id {}", updatedItem.getId());

        return itemMapper.toItemDto(updatedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getById(Long itemId, Long userId) {
        log.info("Getting item {} for user {}", itemId, userId);

        // Проверяем существование пользователя
        if (!userRepository.existsById(userId)) {
            log.error("User with id {} not found", userId);
            throw new NotFoundException("User not found");
        }

        // Получаем item
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Item with id {} not found", itemId);
                    return new NotFoundException("Item not found");
                });

        // Создаем DTO
        ItemDto itemDto = convertToDto(item);

        // Добавляем бронирования только для владельца
        if (item.getOwner().getId().equals(userId)) {
            addBookingInfo(itemDto, item.getId());
        }

        // Добавляем комментарии
        addCommentsInfo(itemDto, item.getId());

        return itemDto;
    }

    private ItemDto convertToDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setRequestId(item.getRequestId());
        return dto;
    }

    private void addBookingInfo(ItemDto itemDto, Long itemId) {
        try {
            LocalDateTime now = LocalDateTime.now();

            // Последнее бронирование
            bookingRepository.findLastBooking(itemId, now)
                    .stream()
                    .findFirst()
                    .ifPresent(booking -> {
                        ItemDto.BookingShort bookingShort = new ItemDto.BookingShort();
                        bookingShort.setId(booking.getId());
                        bookingShort.setBookerId(booking.getBooker().getId());
                        itemDto.setLastBooking(bookingShort);
                    });

            // Следующее бронирование
            bookingRepository.findNextBooking(itemId, now)
                    .stream()
                    .findFirst()
                    .ifPresent(booking -> {
                        ItemDto.BookingShort bookingShort = new ItemDto.BookingShort();
                        bookingShort.setId(booking.getId());
                        bookingShort.setBookerId(booking.getBooker().getId());
                        itemDto.setNextBooking(bookingShort);
                    });
        } catch (Exception e) {
            log.error("Error adding booking info for item {}", itemId, e);
        }
    }

    private void addCommentsInfo(ItemDto itemDto, Long itemId) {
        try {
            List<CommentDto> comments = commentRepository.findByItemId(itemId)
                    .stream()
                    .map(comment -> {
                        CommentDto dto = new CommentDto();
                        dto.setId(comment.getId());
                        dto.setText(comment.getText());
                        dto.setAuthorName(comment.getAuthor().getName());
                        dto.setCreated(comment.getCreated());
                        return dto;
                    })
                    .collect(Collectors.toList());
            itemDto.setComments(comments);
        } catch (Exception e) {
            log.error("Error adding comments info for item {}", itemId, e);
            itemDto.setComments(Collections.emptyList());
        }
    }

    @Override
    public List<ItemDto> getAllByOwner(Long ownerId, PageRequest pageRequest) {
        return List.of();
    }

    @Override
    public List<ItemDto> search(String text, PageRequest pageRequest) {
        return List.of();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getAllByOwner(Long ownerId, Pageable pageable) {
        log.info("Getting all items for owner {}", ownerId);
        return itemRepository.findByOwnerIdOrderById(ownerId, pageable).stream()
                .map(item -> enrichAndConvertToDto(item, ownerId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Object> search(String text, Pageable pageable) {
        log.info("Searching items by text: {}", text);
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
        log.info("Adding comment to item {} by user {}", itemId, userId);
        User author = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with id {} not found", userId);
                    return new NotFoundException("User not found");
                });

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Item with id {} not found", itemId);
                    return new NotFoundException("Item not found");
                });

        validateUserBookedItem(itemId, userId);

        Comment comment = commentMapper.toEntity(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        log.debug("Added comment with id {}", savedComment.getId());

        return commentMapper.toDto(savedComment);
    }

    private ItemDto enrichAndConvertToDto(Item item, Long ownerId) {
        ItemDto itemDto = itemMapper.toItemDto(item);
        enrichItemDtoWithAdditionalData(itemDto, item, ownerId);
        return itemDto;
    }

    private void enrichItemDtoWithAdditionalData(ItemDto itemDto, Item item, Long ownerId) {
        if (item.getOwner().getId().equals(ownerId)) {
            LocalDateTime now = LocalDateTime.now();
            addBookingInfo(itemDto, item.getId());
        }
        addCommentsInfo(itemDto, item.getId());
    }

    private void validateUserBookedItem(Long itemId, Long userId) {
        List<Booking> bookings = bookingRepository.findCompletedBookings(
                itemId, userId, LocalDateTime.now());

        if (bookings.isEmpty()) {
            log.error("User {} never booked item {}", userId, itemId);
            throw new InvalidCommentException("User never booked this item");
        }
    }
}



/*package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.Booking;
import ru.practicum.booking.BookingRepository;
import ru.practicum.exception.InvalidCommentException;
import ru.practicum.exception.NotFoundException;
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
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        log.info("Creating item for owner {}", ownerId);
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> {
                    log.error("User with id {} not found", ownerId);
                    return new NotFoundException("User not found");
                });

        Item item = itemMapper.toItem(itemDto, owner);

        Item savedItem = itemRepository.save(item);
        log.debug("Created item with id {}", savedItem.getId());

        return itemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Long ownerId) {
        log.info("Updating item {} for owner {}", itemDto.getId(), ownerId);
        Item existingItem = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> {
                    log.error("Item with id {} not found", itemDto.getId());
                    return new NotFoundException("Item not found");
                });

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            log.error("User {} is not owner of item {}", ownerId, itemDto.getId());
            throw new NotFoundException("Only owner can update item");
        }

        itemMapper.updateItemFromDto(itemDto, existingItem);
        Item updatedItem = itemRepository.save(existingItem);
        log.debug("Updated item with id {}", updatedItem.getId());

        return itemMapper.toItemDto(updatedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getById(Long id, Long ownerId) {
        log.info("Getting item {} for user {}", id, ownerId);
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Item with id {} not found", id);
                    return new NotFoundException("Item not found");
                });

        return enrichAndConvertToDto(item, ownerId);
    }

    @Override
    public List<ItemDto> getAllByOwner(Long ownerId, PageRequest pageRequest) {
        return List.of();
    }

    @Override
    public List<ItemDto> search(String text, PageRequest pageRequest) {
        return List.of();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getAllByOwner(Long ownerId, Pageable pageable) {
        log.info("Getting all items for owner {}", ownerId);
        return itemRepository.findByOwnerIdOrderById(ownerId, pageable).stream()
                .map(item -> enrichAndConvertToDto(item, ownerId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Object> search(String text, Pageable pageable) {
        log.info("Searching items by text: {}", text);
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
        log.info("Adding comment to item {} by user {}", itemId, userId);
        User author = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with id {} not found", userId);
                    return new NotFoundException("User not found");
                });

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Item with id {} not found", itemId);
                    return new NotFoundException("Item not found");
                });

        validateUserBookedItem(itemId, userId);

        Comment comment = commentMapper.toEntity(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        log.debug("Added comment with id {}", savedComment.getId());

        return commentMapper.toDto(savedComment);
    }

    private ItemDto enrichAndConvertToDto(Item item, Long ownerId) {
        ItemDto itemDto = itemMapper.toItemDto(item);
        enrichItemDtoWithAdditionalData(itemDto, item, ownerId);
        return itemDto;
    }

    private void enrichItemDtoWithAdditionalData(ItemDto itemDto, Item item, Long ownerId) {
        if (item.getOwner().getId().equals(ownerId)) {
            LocalDateTime now = LocalDateTime.now();
            addBookingInfo(itemDto, item.getId(), now);
        }
        addCommentsInfo(itemDto, item.getId());
    }

    private void addBookingInfo(ItemDto itemDto, Long itemId, LocalDateTime now) {
        bookingRepository.findLastBooking(itemId, now).stream()
                .findFirst()
                .ifPresent(booking -> itemDto.setLastBooking(
                        new ItemDto.BookingShort(booking.getId(), booking.getBooker().getId())));

        bookingRepository.findNextBooking(itemId, now).stream()
                .findFirst()
                .ifPresent(booking -> itemDto.setNextBooking(
                        new ItemDto.BookingShort(booking.getId(), booking.getBooker().getId())));
    }

    private void addCommentsInfo(ItemDto itemDto, Long itemId) {
        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
        itemDto.setComments(comments);
    }

    private void validateUserBookedItem(Long itemId, Long userId) {
        List<Booking> bookings = bookingRepository.findCompletedBookings(
                itemId, userId, LocalDateTime.now());

        if (bookings.isEmpty()) {
            log.error("User {} never booked item {}", userId, itemId);
            throw new InvalidCommentException("User never booked this item");
        }
    }
}
*/