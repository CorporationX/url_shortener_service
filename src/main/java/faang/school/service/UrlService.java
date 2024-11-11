package faang.school.service;

public interface UrlService {

    String redirectByHash(String hash);

    String getUrlBy(String hash);

    String getUrlFromDatabaseBy(String hash);
}
