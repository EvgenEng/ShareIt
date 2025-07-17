package ru.practicum.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.client.BaseClient;
import ru.practicum.exception.ErrorResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> {
                            HttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                                    .build();
                            return new HttpComponentsClientHttpRequestFactory(
                                    HttpClientBuilder.create()
                                            .setConnectionManager(connectionManager)
                                            .build());
                        })
                        .build()
        );
    }

    public ResponseEntity<Object> create(long userId, BookingDto requestDto) {
        if (requestDto == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Booking data cannot be null"));
        }
        if (requestDto.getItemId() == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Item ID cannot be null"));
        }
        if (requestDto.getStart() == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Start date cannot be null"));
        }
        if (requestDto.getEnd() == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("End date cannot be null"));
        }
        if (requestDto.getStart().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Start date must be in future"));
        }
        if (!requestDto.getEnd().isAfter(requestDto.getStart())) {
            return ResponseEntity.badRequest().body(new ErrorResponse("End date must be after start date"));
        }

        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> approve(long userId, long bookingId, boolean approved) {
        if (bookingId <= 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Booking ID must be positive"));
        }
        Map<String, Object> parameters = Map.of("approved", approved);
        return patch("/" + bookingId, userId, parameters);
    }

    public ResponseEntity<Object> getById(long userId, long bookingId) {
        if (bookingId <= 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Booking ID must be positive"));
        }
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getUserBookings(long userId, String state, int from, int size) {
        if (from < 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse("From parameter cannot be negative"));
        }
        if (size <= 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Size parameter must be positive"));
        }

        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getOwnerBookings(long userId, String state, int from, int size) {
        if (from < 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse("From parameter cannot be negative"));
        }
        if (size <= 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Size parameter must be positive"));
        }

        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }
}
