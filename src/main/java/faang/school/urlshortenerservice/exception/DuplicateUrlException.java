package faang.school.urlshortenerservice.exception;

public class DuplicateUrlException extends RuntimeException{
    public DuplicateUrlException(String message){
        super(message);
    }
}
