package ru.practicum.shareitserver.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareitserver.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItemId(Long itemId);

    List<Comment> findByAuthorId(Long authorId);
}
