package faang.school.urlshortenerservice.locale;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Locale;

@Slf4j
public class LocaleChangeFilter extends OncePerRequestFilter {

    //@Value("${locale.request.name-parameter-in-header}")
    private String acceptLanguageName = "Accept-Language";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String acceptLanguage = request.getHeader(acceptLanguageName);
        if (StringUtils.hasText(acceptLanguage)) {
            String[] languages = acceptLanguage.split(",");
            for (String lang : languages) {
                String[] parts = lang.split("-");
                if (parts.length == 2) {
                    String language = parts[0];
                    String country = parts[1];

                    Locale locale = new Locale(language, country);
                    LocaleContextHolder.setLocale(locale);
                    break;
                }
            }
        }
        if (LocaleContextHolder.getLocale() == null) {
            LocaleContextHolder.setLocale(Locale.ENGLISH);
        }
        filterChain.doFilter(request, response);
    }
}