package faang.school.urlshortenerservice.exception;

public class UrlNotExistException extends RuntimeException {

    final Long notExistentUlrId;

    public UrlNotExistException(long id) {
        super(String.format("Url with id %s does not exist", id));
        this.notExistentUlrId = id;
    }
}
