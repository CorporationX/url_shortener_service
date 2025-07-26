package faang.school.urlshortenerservice.handler;

@FunctionalInterface
public interface ErrorHandler {
    String handle(Exception ex);
}
