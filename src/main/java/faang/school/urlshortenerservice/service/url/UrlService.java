package faang.school.urlshortenerservice.service.url;

import org.springframework.web.servlet.view.RedirectView;

public interface UrlService {

    RedirectView getRedirectView(String hash);
}
