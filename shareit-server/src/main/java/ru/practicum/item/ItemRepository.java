package ru.practicum.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.booking.Booking;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // Получение вещей владельца с пагинацией
    List<Item> findByOwnerIdOrderById(Long ownerId, Pageable pageable);

    // Поиск доступных вещей по тексту с пагинацией
    @Query("SELECT i FROM Item i " +
            "WHERE i.available = true AND " +
            "(LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))")
    List<Item> search(@Param("text") String text, Pageable pageable);

    // Получение вещей по ID запроса
    List<Item> findByRequestId(Long requestId);

    // Получение вещей по списку ID запросов
    List<Item> findByRequestIdIn(List<Long> requestIds);

    // Проверка существования вещи по владельцу
    boolean existsByIdAndOwnerId(Long itemId, Long ownerId);

    // Поиск последнего бронирования для вещи
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId AND " +
            "b.start < CURRENT_TIMESTAMP AND " +
            "b.status = 'APPROVED' " +
            "ORDER BY b.start DESC")
    List<Booking> findLastBookingForItem(@Param("itemId") Long itemId, Pageable pageable);
}
