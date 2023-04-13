package ru.practicum.shareitserver.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareitserver.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findItemsByOwnerId(Long userId, Pageable pageable);

    @Query("select i from Item i " +
            "where (lower(i.name) like concat('%', lower(:text),'%') " +
            "or lower(i.description) like concat('%', lower(:text), '%')) " +
            "and i.available = true")
    Page<Item> searchItem(@Param("text") String text, Pageable pageable);

}
