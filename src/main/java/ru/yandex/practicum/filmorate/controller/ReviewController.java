package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.dto.ReviewCreateDto;
import ru.yandex.practicum.filmorate.controller.dto.ReviewGetDto;
import ru.yandex.practicum.filmorate.controller.dto.ReviewUpdateDto;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.services.ReviewService;

import java.util.List;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,
        makeFinal = true)
public class ReviewController {
    ReviewService reviewService;

    @PostMapping()
    public ResponseEntity<ReviewGetDto> createReview(@Valid @RequestBody ReviewCreateDto reviewCreateDto) {
        Review review = Review.builder()
                .content(reviewCreateDto.getContent())
                .isPositive(reviewCreateDto.getIsPositive())
                .filmId(reviewCreateDto.getFilmId())
                .userId(reviewCreateDto.getUserId())
                .useful(reviewCreateDto.getUseful())
                .build();
        log.info("Creating review: {}", review);
        Review createdReview = reviewService.createReview(review);
        log.info("Review created successfully: {}", createdReview);
        return ResponseEntity.ok(toReviewGetDto(createdReview));
    }

    @PutMapping()
    public ResponseEntity<ReviewGetDto> updateReview(@Valid @RequestBody ReviewUpdateDto reviewUpdateDto) {
        Review review = Review.builder()
                .reviewId(reviewUpdateDto.getReviewId())
                .content(reviewUpdateDto.getContent())
                .isPositive(reviewUpdateDto.getIsPositive())
                .filmId(reviewUpdateDto.getFilmId())
                .userId(reviewUpdateDto.getUserId())
                .useful(reviewUpdateDto.getUseful())
                .build();
        log.info("Updating review: {}", review);
        Review updatedReview = reviewService.updateReview(review);
        log.info("Review updated successfully: {}", updatedReview);
        return ResponseEntity.ok(toReviewGetDto(updatedReview));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewGetDto> getReviewById(@PathVariable int id) {
        log.info("Fetching review by ID: {}", id);
        Review review = reviewService.getReviewById(id);
        log.info("Review fetched successfully: {}", review);
        return ResponseEntity.ok(toReviewGetDto(review));
    }

    @GetMapping()
    public ResponseEntity<List<ReviewGetDto>> getReviews(
            @RequestParam(name = "filmId", defaultValue = "-1") int filmId,
            @RequestParam(name = "count", defaultValue = "10") int count) {
        log.info("Fetching reviews with parameters: filmId={}, count={}", filmId, count);
        List<Review> reviews = reviewService.getReviewByFilmId(filmId, count);
        log.info("Fetched {} reviews successfully", reviews.size());

        List<ReviewGetDto> reviewsGetDto = reviews.stream()
                .map(this::toReviewGetDto)
                .toList();

        return ResponseEntity.ok(reviewsGetDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable int id) {
        log.info("Deleting review with ID: {}", id);
        reviewService.deleteReview(id);
        log.info("Review with ID {} deleted successfully", id);
        return ResponseEntity.ok("");
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<?> addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Adding like to review with ID: {}, by user: {}", id, userId);
        reviewService.likeReview(id, userId, true);
        log.info("Like added successfully to review with ID: {}, by user: {}", id, userId);
        return ResponseEntity.ok("");
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<?> deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Removing like from review with ID: {}, by user: {}", id, userId);
        reviewService.removeLike(id, userId, true);
        log.info("Like removed successfully from review with ID: {}, by user: {}", id, userId);
        return ResponseEntity.ok("");
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ResponseEntity<?> addDislike(@PathVariable int id, @PathVariable int userId) {
        log.info("Adding dislike to review with ID: {}, by user: {}", id, userId);
        reviewService.likeReview(id, userId, false);
        log.info("Dislike added successfully to review with ID: {}, by user: {}", id, userId);
        return ResponseEntity.ok("");
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ResponseEntity<?> deleteDislike(@PathVariable int id, @PathVariable int userId) {
        log.info("Removing dislike from review with ID: {}, by user: {}", id, userId);
        reviewService.removeLike(id, userId, false);
        log.info("Dislike removed successfully from review with ID: {}, by user: {}", id, userId);
        return ResponseEntity.noContent().build();
    }

    public ReviewGetDto toReviewGetDto(Review review) {
        return ReviewGetDto.builder()
                .reviewId(review.getReviewId())
                .content(review.getContent())
                .filmId(review.getFilmId())
                .userId(review.getUserId())
                .isPositive(review.isPositive())
                .useful(review.getUseful())
                .build();
    }
}