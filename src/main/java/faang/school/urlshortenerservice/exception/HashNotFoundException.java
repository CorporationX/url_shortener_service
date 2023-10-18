package faang.school.urlshortenerservice.exception;

public class HashNotFoundException extends BusinessException{
    public HashNotFoundException(String code, String message) {
        super(code, message);
    }
}
