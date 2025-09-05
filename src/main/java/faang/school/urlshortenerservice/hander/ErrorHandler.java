package faang.school.urlshortenerservice.hander;

@FunctionalInterface
public interface ErrorHandler {
    String handle(Exception ex);
}