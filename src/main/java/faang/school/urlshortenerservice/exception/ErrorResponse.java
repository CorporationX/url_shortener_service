package faang.school.urlshortenerservice.exception;

public class ErrorResponse extends RuntimeException{
    public ErrorResponse(String message) {
        super(message);
    }
}
