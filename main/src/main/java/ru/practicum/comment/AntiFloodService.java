package ru.practicum.comment;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.practicum.exceptions.RateLimitException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

@Component
public class AntiFloodService {
    // Мапа для храненния ID пользователя и время его последнего комментария
    private final HashMap<Long, LocalDateTime> lastCommentTime = new HashMap<>();

    // Минимальное кол-во секунд между комментариями
    private static final int MIN_SECONDS_BETWEEN_COMMENTS = 10;
    // Временной интервал долше готорого данные не храняться в мапе
    private static final Duration CLEANUP_INTERVAL_IN_MINUTS = Duration.ofMinutes(5);

    // Проверяем что пользователь оставли последний комментарий не ранее MIN_SECONDS_BETWEEN_COMMENTS секунд назад
    public void checkCommentAllowed(Long userId) {
        LocalDateTime commentTime = lastCommentTime.get(userId);
        LocalDateTime now = LocalDateTime.now();
        if (commentTime != null) {
            Duration sinceLast = Duration.between(commentTime, now);
            if (sinceLast.toSeconds() > MIN_SECONDS_BETWEEN_COMMENTS) {
                throw new RateLimitException("Too many comments. Wait " +
                        (MIN_SECONDS_BETWEEN_COMMENTS - sinceLast.toSeconds()) + " seconds");
            }
        }

        // Записываем время последнего комментария в мапу
        lastCommentTime.put(userId, now);
    }

    // Очищаем мапу от старых записей
    @Scheduled(fixedRateString = "${antiflood.cleanup.interval}")
    public void cleanupOldRecords() {
        LocalDateTime now = LocalDateTime.now();
        lastCommentTime.entrySet().removeIf(entry ->
                Duration.between(entry.getValue(), now).compareTo(CLEANUP_INTERVAL_IN_MINUTS) > 0
        );
    }
}
