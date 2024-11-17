package faang.school.urlshortenerservice.service.url;

public interface UrlService {

    String getLongUrl(String hash);

    String getShortUrl(String hash);
}
