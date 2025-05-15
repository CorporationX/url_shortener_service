package faang.school.urlshortenerservice.dto;

public record ApiError(String message, int status, String method, String path, String timestamp) {
    private static final String toFilterString = """
            {
                "message": "%s",
                "status": "%s",
                "method": "%s",
                "path": "%s",
                "timestamp": "%s",
                }
            """;
    public String toFilterString() {
        return String.format(toFilterString, message, status, method, path, timestamp);
    }
}