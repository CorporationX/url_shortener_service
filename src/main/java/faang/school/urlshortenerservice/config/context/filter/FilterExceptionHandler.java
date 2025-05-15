package faang.school.urlshortenerservice.config.context.filter;

import faang.school.urlshortenerservice.dto.ApiError;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.TriConsumer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class FilterExceptionHandler {
    private final Map<Class<? extends Exception>, TriConsumer<HttpServletRequest, HttpServletResponse, Exception>>
            exceptions = Map.of(
            SignatureException.class, (request, response, ex) ->
                    handle(request, response, HttpServletResponse.SC_UNAUTHORIZED, ex),
            MalformedJwtException.class, (request, response, ex) ->
                    handle(request, response, HttpServletResponse.SC_UNAUTHORIZED, ex),
            ExpiredJwtException.class, (request, response, ex) ->
                    handle(request, response, HttpServletResponse.SC_UNAUTHORIZED, ex),
            RuntimeException.class, (request, response, ex) ->
                    handle(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex)
    );

    public void handleException(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        log.error("Exception handled in filter: ", ex);
        Optional.ofNullable(exceptions.get(ex.getClass()))
                .ifPresent(handle -> handle.accept(request, response, ex));
    }

    private void handle(HttpServletRequest request, HttpServletResponse response, int status, Exception ex)  {
        try {
            response.setStatus(status);
            response.setContentType("application/json");
            response.getWriter().write(getBody(request, status, ex));
            response.getWriter().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getBody(HttpServletRequest request, int status, Exception ex) {
        return new ApiError(
                ex.getMessage(),
                status,
                request.getMethod(),
                request.getRequestURI() + "?" + request.getQueryString(),
                Instant.now().toString()).toFilterString();
    }
}
