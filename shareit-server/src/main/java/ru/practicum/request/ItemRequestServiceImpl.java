package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.ItemRepository;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemResponseDto;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requestor);
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.toDto(savedRequest, getItemsForRequest(savedRequest.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getUserRequests(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        return itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId, pageable).stream()
                .map(request -> ItemRequestMapper.toDto(
                        request,
                        getItemsForRequest(request.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllRequests(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        return itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId, pageable).stream()
                .map(request -> ItemRequestMapper.toDto(
                        request,
                        getItemsForRequest(request.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        return ItemRequestMapper.toDto(itemRequest, getItemsForRequest(requestId));
    }

    private List<ItemResponseDto> getItemsForRequest(Long requestId) {
        return itemRepository.findByRequestId(requestId).stream()
                .map(item -> new ItemResponseDto(
                        item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.getAvailable(),
                        requestId))
                .collect(Collectors.toList());
    }
}
