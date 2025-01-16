package faang.school.urlshortenerservice.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ValidationAspect {

    @Before("execution(* faang.school.urlshortenerservice.service.UrlService.createShortUrl(..)) && args(longUrl,..)")
    public void validateUrlFormat(String longUrl) {
        if (!longUrl.matches("(https?://)?(www.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&//=]*)")) {
            log.error("Invalid URL format: {}", longUrl);
            throw new IllegalArgumentException("Invalid URL format.");
        }
    }
}
