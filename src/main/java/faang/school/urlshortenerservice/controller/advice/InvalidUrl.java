package faang.school.urlshortenerservice.controller.advice;

public class InvalidUrl extends RuntimeException {
    public InvalidUrl(String message) {
        super(message);
    }
}