package faang.school.urlshortenerservice.service;

public interface UrlService {

    String redirectByHash(String hash);

    String getUrlBy(String hash);

    String getUrlFromDatabaseBy(String hash);
}
