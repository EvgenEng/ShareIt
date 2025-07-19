package ru.practicum.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequestorIdOrderByCreatedDesc(Long requestorId, Pageable pageable);

    Page<ItemRequest> findByRequestorIdNotOrderByCreatedDesc(Long requestorId, Pageable pageable);
}
