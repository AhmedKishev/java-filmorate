package ru.yandex.practicum.filmorate.dao;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.FeedRowMapper;
import ru.yandex.practicum.filmorate.model.FeedEvent;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,
        makeFinal = true)
public class FeedDbStorage {
    static String ADD_EVENT =
            "INSERT INTO feed (user_id, event_type, operation, entity_id, timestamp) VALUES (?, ?, ?, ?, ?)";
    static String GET_USER_FEED =
            "SELECT * FROM feed WHERE user_id = ? ORDER BY timestamp DESC";
    JdbcTemplate jdbcTemplate;
    FeedRowMapper feedRowMapper;

    public void addEvent(FeedEvent event) {
        jdbcTemplate.update(ADD_EVENT,
                event.getUserId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getEntityId(),
                event.getTimestamp());
    }


    public List<FeedEvent> getUserFeed(Long userId) {
        List<FeedEvent> list = jdbcTemplate.query(GET_USER_FEED, feedRowMapper, userId);
        Collections.reverse(list);
        return list;
    }
}
