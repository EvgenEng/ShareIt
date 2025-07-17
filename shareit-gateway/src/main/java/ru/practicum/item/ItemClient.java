package ru.practicum.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;
import ru.practicum.exception.ErrorResponse;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit.server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(long userId, ItemDto itemDto) {
        if (itemDto == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Item data cannot be null"));
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Item name cannot be empty"));
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Item description cannot be empty"));
        }
        if (itemDto.getAvailable() == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Item availability must be specified"));
        }

        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> update(long userId, ItemDto itemDto) {
        if (itemDto == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Item data cannot be null"));
        }
        if (itemDto.getId() == null || itemDto.getId() <= 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid item ID"));
        }

        return patch("/" + itemDto.getId(), userId, Map.of());
    }

    public ResponseEntity<Object> getById(long itemId, long userId) {
        if (itemId <= 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Item ID must be positive"));
        }
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllByOwner(long userId, int from, int size) {
        if (from < 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse("From parameter cannot be negative"));
        }
        if (size <= 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Size parameter must be positive"));
        }

        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("", userId, parameters);
    }

    public ResponseEntity<Object> search(String text, int from, int size) {
        if (from < 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse("From parameter cannot be negative"));
        }
        if (size <= 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Size parameter must be positive"));
        }

        if (text == null || text.isBlank()) {
            return ResponseEntity.ok().body(List.of());
        }

        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search", 0L, parameters);
    }

    public ResponseEntity<Object> addComment(long userId, long itemId, CommentDto commentDto) {
        if (itemId <= 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Item ID must be positive"));
        }
        if (commentDto == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Comment data cannot be null"));
        }
        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Comment text cannot be empty"));
        }

        return post("/" + itemId + "/comment", userId, commentDto);
    }
}

/*package ru.practicum.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit.server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> update(long userId, ItemDto itemDto) {
        return patch("/" + itemDto.getId(), userId, itemDto);
    }

    public ResponseEntity<Object> getById(long itemId, long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllByOwner(long userId, int from, int size) {
        Map<String, Object> parameters = Map.of("from", from, "size", size);
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> search(String text, int from, int size) {
        if (text.isBlank()) {
            return ResponseEntity.ok().body(List.of());
        }
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", 0L, parameters);
    }

    public ResponseEntity<Object> addComment(long userId, long itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);  // Убедиться, что URL правильный
    }

    protected ResponseEntity<Object> patch(String path, long userId, Object body) {
        return exchange(path, userId, Map.of(), body, HttpMethod.PATCH);
    }
}

/*package ru.practicum.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit.server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> update(long userId, ItemDto itemDto) {
        return patch("/" + itemDto.getId(), userId, Map.of());
    }

    public ResponseEntity<Object> getById(long itemId, long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllByOwner(long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("", userId, parameters);
    }

    public ResponseEntity<Object> search(String text, int from, int size) {
        if (text.isBlank()) {
            return ResponseEntity.ok().body(List.of());
        }
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search", 0L, parameters);
    }

    public ResponseEntity<Object> addComment(long userId, long itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }

    public ResponseEntity<Object> createItem(long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }
}
*/