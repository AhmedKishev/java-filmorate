package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.services.FeedService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,
        makeFinal = true)
@RequestMapping("/users")
public class FeedController {
    private final FeedService feedService;

    @GetMapping("/{id}/feed")
    public List<FeedEvent> getUserFeed(@PathVariable Long id) {
        return feedService.getUserFeed(id);
    }
}
