package faang.school.urlshortenerservice.exception;

public class UrlNotFoundException extends BusinessException{
    public UrlNotFoundException(String code, String message) {
        super(code, message);
    }
}
