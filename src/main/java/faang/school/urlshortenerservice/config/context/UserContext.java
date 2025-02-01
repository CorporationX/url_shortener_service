package faang.school.urlshortenerservice.config.context;


import org.springframework.stereotype.Component;

@Component
public class UserContext implements AutoCloseable {

    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public long getUserId() {
        Long userId = userIdHolder.get();
        if (userId == null) {
            throw new IllegalArgumentException("Missing required header 'x-user-id'.");
        }
        return userId;
    }

    @Override
    public void close() {
        userIdHolder.remove();
    }

    public void clear() {
        userIdHolder.remove();
    }
}