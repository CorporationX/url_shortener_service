package faang.school.urlshortenerservice.config.context;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class UserHeaderFilter extends OncePerRequestFilter {

    private static final String USER_ID_HEADER = "x-user-id";
    private static final String HTTP_METHOD_GET = "GET";

    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final Pattern REDIRECT_TAIL_PATTERN = Pattern.compile("^/[0-9A-Za-z]{6}$");
    private static final String MISSING_USER_ID_JSON = """
        {
          "code": "bad_request",
          "message": "Missing required header 'x-user-id'. Please include 'x-user-id' with a valid user ID."
        }
        """;
    private static final String INVALID_USER_ID_JSON = """
            {"code":"bad_request","message":"Invalid 'x-user-id' header value. Must be a number."}
            """;

    private final UserContext userContext;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!HTTP_METHOD_GET.equalsIgnoreCase(request.getMethod())) {
            return false;
        }

        String uri = request.getRequestURI();
        String ctx = request.getContextPath();

        if (uri == null || ctx == null || !uri.startsWith(ctx)) {
            return false;
        }

        String tail = uri.substring(ctx.length());
        return REDIRECT_TAIL_PATTERN.matcher(tail).matches();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Long userId = parseUserIdHeader(request, response);
        if (userId == null) {
            return;
        }

        userContext.setUserId(userId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            userContext.clear();
        }
    }

    private Long parseUserIdHeader(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String rawUserId = request.getHeader(USER_ID_HEADER);
        if (rawUserId == null || rawUserId.isBlank()) {
            writeBadRequest(response, MISSING_USER_ID_JSON);
            return null;
        }

        try {
            return Long.parseLong(rawUserId.trim());
        } catch (NumberFormatException ex) {
            writeBadRequest(response, INVALID_USER_ID_JSON);
            return null;
        }
    }

    private void writeBadRequest(HttpServletResponse response, String body) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType(CONTENT_TYPE_JSON);
        response.getWriter().write(body);
    }
}