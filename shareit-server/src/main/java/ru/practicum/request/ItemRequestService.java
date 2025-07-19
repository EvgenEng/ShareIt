package ru.practicum.request;

import org.springframework.data.domain.Pageable;
import ru.practicum.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getUserRequests(Long userId, Pageable pageable);

    List<ItemRequestDto> getAllRequests(Long userId, Pageable pageable);

    ItemRequestDto getRequestById(Long userId, Long requestId);
}
