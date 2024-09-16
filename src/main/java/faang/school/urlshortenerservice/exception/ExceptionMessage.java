package faang.school.urlshortenerservice.exception;

public final class ExceptionMessage {
    private ExceptionMessage() {
    }

    public final static String URL_NOT_FOUND = "URL not found for hash: ";
    public final static String  DESERIALIZATION_IN_OBJECT = "Error when converting json {} to object";
    public final static String  SERIALIZATION_IN_OBJECT = "Error when converting object {} to json";
    public final static String EXCEPTION_IN_SAVE = "Hash collision or other data integrity violation. ";
}