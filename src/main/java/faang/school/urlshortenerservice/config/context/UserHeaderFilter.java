package faang.school.urlshortenerservice.config.context;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class UserHeaderFilter implements Filter {

    private final UserContext userContext;
    private final MessageSource messageSource;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        String userId = req.getHeader("x-user-id");
        if (userId != null) {
            userContext.setUserId(Long.parseLong(userId));
        } else {
            throw new IllegalArgumentException(
                    messageSource.getMessage("exception.illegal.argument",
                            null,
                            LocaleContextHolder.getLocale()));
        }
        try {
            chain.doFilter(request, response);
        } finally {
            userContext.clear();
        }
    }
}
