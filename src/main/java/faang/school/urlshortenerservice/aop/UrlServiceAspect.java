package faang.school.urlshortenerservice.aop;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@Component
@Aspect
@Slf4j
public class UrlServiceAspect {

    @Pointcut(value = "execution(* faang.school.urlshortenerservice.service.UrlService.saveNewHash(faang.school.urlshortenerservice.dto.UrlDto)) && args(urlDto)",
            argNames = "urlDto")
    public void saveNewHashPointCut(UrlDto urlDto) {
        //TODO
    }

    @Pointcut(value = "execution(* faang.school.urlshortenerservice.service.UrlService.searchUrl(*))")
    public void searchUrlPointCut() {
        //TODO
    }

    @Before(value = "saveNewHashPointCut(faang.school.urlshortenerservice.dto.UrlDto) && args(urlDto)",
            argNames = "urlDto")
    public void saveNewHashBefore(UrlDto urlDto) {
        try {
            URL url = new URL(urlDto.url());
            url.toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            log.error("Валидация НЕ пройдена!");
            throw new ValidationException(e.getMessage());
        }
    }

    @Before(value = "searchUrlPointCut()")
    public void searchUrlBefore() {
        log.info("Начали поиск УРЛ в кеше ");
    }
}
