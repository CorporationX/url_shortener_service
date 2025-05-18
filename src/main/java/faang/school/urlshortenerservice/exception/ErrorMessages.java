package faang.school.urlshortenerservice.exception;

public class ErrorMessages {
    public static final String CACHE_REFILL_FAILED = "Cache refill failed";
    public static final String HASH_GENERATION_FAILED = "Hash generation failed";
    public static final String DATABASE_ACCESS_ERROR = "Database access error";
    public static final String HASH_NOT_FOUND = "Hash not found: %s";
    public static final String URL_NOT_FOUND = "URL not found for hash: %s";
    public static final String VALIDATION_ERROR_LOG_TEMPLATE = "Validation error: {}";
    public static final String NOT_FOUND_ERROR_LOG_TEMPLATE = "{}";
    public static final String SYSTEM_ERROR_LOG_TEMPLATE = "System error: {}";
    public static final String UNEXPECTED_ERROR_LOG_TEMPLATE = "Unexpected error: {}";
    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error";
}
