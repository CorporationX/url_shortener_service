package faang.school.urlshortenerservice.exception.global;

@FunctionalInterface
public interface ErrorHandler {
    String handle(Exception ex);
}
