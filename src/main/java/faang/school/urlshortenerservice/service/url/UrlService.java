package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.dto.Request;
import faang.school.urlshortenerservice.dto.Response;
import org.springframework.web.servlet.view.RedirectView;

public interface UrlService {

    RedirectView getRedirectView(String hash);

    Response createShortUrl(Request dto);
}
