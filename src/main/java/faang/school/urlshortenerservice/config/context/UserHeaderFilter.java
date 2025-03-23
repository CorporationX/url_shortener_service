package faang.school.urlshortenerservice.config.context;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserHeaderFilter implements Filter {

    private final UserContext userContext;

    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/swagger-ui",
            "/v3/api-docs",
            "/swagger-resources",
            "/webjars"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI();
        if (isSwaggerPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        String userId = req.getHeader("x-user-id");
        if (userId != null) {
            userContext.setUserId(Long.parseLong(userId));
        } else {
            throw new IllegalArgumentException("Missing required header 'x-user-id'. Please include 'x-user-id' header with a valid user ID in your request.");
        }
        try {
            chain.doFilter(request, response);
        } finally {
            userContext.clear();
        }
    }

    private boolean isSwaggerPath(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }
}
