package faang.school.urlshortenerservice.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String emailAlreadyExists) {
        super(emailAlreadyExists);
    }
}
