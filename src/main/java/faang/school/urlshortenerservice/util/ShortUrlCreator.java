package faang.school.urlshortenerservice.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;


@Component
public class ShortUrlCreator {
    public String createShortUrl(HttpServletRequest httpServletRequest, String hash) {
        return (httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName() + "/" + hash);
    }
}