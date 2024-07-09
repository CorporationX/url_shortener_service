package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.dto.UrlDto;
import org.springframework.web.servlet.view.RedirectView;

public interface UrlService {

    RedirectView getRedirectView(String hash);

    String createShortUrl(UrlDto dto);
}
