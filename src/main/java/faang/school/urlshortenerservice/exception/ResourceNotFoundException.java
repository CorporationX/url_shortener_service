package faang.school.urlshortenerservice.exception;

public class ResourceNotFoundException extends RuntimeException {
    private static final String RESOURCE_NOT_FOUND = "%s %s not found";

    public ResourceNotFoundException(String resourceName, Object resourceId) {
        super(String.format(RESOURCE_NOT_FOUND, resourceName, resourceId));
    }
}
