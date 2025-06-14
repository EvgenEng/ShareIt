package ru.practicum.item;

import org.springframework.stereotype.Service;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    public ItemServiceImpl(ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        // Проверяем существование пользователя
        userService.getById(ownerId);

        Item item = ItemMapper.toItem(itemDto, ownerId);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long ownerId) {
        Item existingItem = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));

        if (!existingItem.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("Только владелец может обновлять предмет");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(existingItem));
    }

    @Override
    public ItemDto getById(Long id, Long ownerId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllByOwner(Long ownerId) {
        // Проверяем существование пользователя
        userService.getById(ownerId);

        return itemRepository.findAllByOwnerId(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
