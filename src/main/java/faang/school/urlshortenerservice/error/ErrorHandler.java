package faang.school.urlshortenerservice.error;

@FunctionalInterface
public interface ErrorHandler {
    String handle(Exception ex);
}
