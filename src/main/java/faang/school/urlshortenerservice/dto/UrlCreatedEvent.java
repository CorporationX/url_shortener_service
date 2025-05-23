package faang.school.urlshortenerservice.dto;

public record UrlCreatedEvent(String hash, String originalUrl) {}