package faang.school.urlshortenerservice.config.context;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class UserHeaderFilter implements Filter {

    private final UserContext userContext;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        String userId = req.getHeader("x-user-id");
        try {
            if (userId != null) {
                userContext.setUserId(Long.parseLong(userId));
            }
            chain.doFilter(request, response);
        } catch (NumberFormatException e) {
            throw new ServletException("Invalid x-user-id header. Expected a long value.");
        } finally {
            userContext.clear();
        }
    }
}