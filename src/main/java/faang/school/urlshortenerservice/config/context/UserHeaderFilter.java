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

        try (UserContext ignored = userContext) {
            if (userIdHeader == null) {
                throw new IllegalArgumentException("Missing required header 'x-user-id'. Please include 'x-user-id' header with a valid user ID in your request.");
            }
            long userId = Long.parseLong(userIdHeader);
            userContext.setUserId(userId);

            chain.doFilter(request, response);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid 'x-user-id' header. It must be a valid numeric value.", e);
        }
    }
}