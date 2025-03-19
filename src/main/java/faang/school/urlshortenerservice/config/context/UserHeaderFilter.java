package faang.school.urlshortenerservice.config.context;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class UserHeaderFilter implements Filter {

    private final UserContext userContext;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        String userIdHeader = req.getHeader("x-user-id");
        if (userIdHeader != null) {
            try {
                Long userId = Long.parseLong(userIdHeader);
                userContext.setUserId(userId);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid user ID format");
            }
        }
        try {
            chain.doFilter(request, response);
        } finally {
            userContext.clear();
        }
    }
}
