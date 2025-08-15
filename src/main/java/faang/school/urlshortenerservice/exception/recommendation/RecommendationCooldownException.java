package faang.school.urlshortenerservice.exception.recommendation;

public class RecommendationCooldownException extends RuntimeException {
    public RecommendationCooldownException(String message) {
        super(message);
    }
}
