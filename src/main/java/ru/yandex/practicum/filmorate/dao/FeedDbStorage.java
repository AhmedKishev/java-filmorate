package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.FeedRowMapper;
import ru.yandex.practicum.filmorate.model.FeedEvent;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FeedDbStorage {
    private static final String ADD_EVENT =
            "INSERT INTO feed (user_id, event_type, operation, entity_id, timestamp) VALUES (?, ?, ?, ?, ?)";
    private static final String GET_USER_FEED =
            "SELECT * FROM feed WHERE user_id = ? ORDER BY timestamp DESC";
    private final JdbcTemplate jdbcTemplate;
    private final FeedRowMapper feedRowMapper;

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
