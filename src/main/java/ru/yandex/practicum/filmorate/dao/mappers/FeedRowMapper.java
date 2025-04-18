package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FeedEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FeedRowMapper implements RowMapper<FeedEvent> {
    @Override
    public FeedEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
        return FeedEvent.builder()
                .eventId(rs.getLong("event_id"))
                .timestamp(rs.getLong("timestamp"))
                .userId(rs.getLong("user_id"))
                .eventType(FeedEvent.EventType.valueOf(rs.getString("event_type")))
                .operation(FeedEvent.Operation.valueOf(rs.getString("operation")))
                .entityId(rs.getLong("entity_id"))
                .build();
    }
}
