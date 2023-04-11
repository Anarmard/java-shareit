package ru.practicum.shareitgateway.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareitgateway.client.BaseClient;
import ru.practicum.shareitgateway.item.dto.CommentRequestDto;
import ru.practicum.shareitgateway.item.dto.ItemCreateDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    // добавление новой вещи
    public ResponseEntity<Object> add(Long userId, ItemCreateDto itemCreateDto) {
        return post("", userId, itemCreateDto);
    }

    // редактирование вещи
    public ResponseEntity<Object> update(Long userId, Long itemId, ItemCreateDto itemCreateDto) {
        return patch("/" + itemId, userId, itemCreateDto);
    }

    // Просмотр информации о конкретной вещи по её идентификатору
    public ResponseEntity<Object> getById(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    // Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой
    public ResponseEntity<Object> getItemsBooking(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    // Поиск вещи потенциальным арендатором
    public ResponseEntity<Object> getItemsBySearch(String text, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", null, parameters);
    }

    public void deleteItem(Long userId, Long itemId) {
        delete("/" + itemId, userId);
    }

    // Добавление отзывов
    public ResponseEntity<Object> addComment(Long userId, CommentRequestDto commentRequestDto, Long itemId) {
        return post("/" + itemId + "/comment", userId, commentRequestDto);
    }
}
