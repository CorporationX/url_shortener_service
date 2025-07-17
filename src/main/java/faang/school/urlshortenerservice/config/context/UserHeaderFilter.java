package faang.school.urlshortenerservice.config.context;

import faang.school.urlshortenerservice.exception.authorization.UserUnauthorizedException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserHeaderFilter implements Filter {

    private final UserContext userContext;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        String userId = req.getHeader("x-user-id");
        if (userId == null) {
            String errorMsg = "User ID is missing. Please make sure 'x-user-id' header is included in the request.";
            log.error(errorMsg);
            throw new UserUnauthorizedException(errorMsg);
        }
        userContext.setUserId(Long.parseLong(userId));
        try {
            chain.doFilter(request, response);
        } finally {
            userContext.clear();
        }
    }
}
