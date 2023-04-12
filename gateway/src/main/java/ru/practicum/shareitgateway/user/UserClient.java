package ru.practicum.shareitgateway.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareitgateway.client.BaseClient;
import ru.practicum.shareitgateway.user.dto.UserRequestDto;

@Service
public class UserClient  extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getAll() {
        return get("");
    }

    public ResponseEntity<Object> getById(Long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> save(UserRequestDto userRequestDto) {
        return post("", userRequestDto);
    }

    public ResponseEntity<Object> update(Long userId, UserRequestDto userRequestDto) {
        return patch("/" + userId, userRequestDto);
    }

    public void deleteById(Long userId) {
        delete("/" + userId);
    }
}
