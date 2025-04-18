package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeedEvent {
    private Long eventId;
    private Long timestamp;
    private Long userId;
    private EventType eventType;
    private Operation operation;
    private Long entityId;

    public enum EventType {
        LIKE, REVIEW, FRIEND
    }

    public enum Operation {
        ADD, REMOVE, UPDATE
    }
}
