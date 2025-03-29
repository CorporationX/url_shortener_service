package faang.school.urlshortenerservice.exception.handler;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorResponse {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private String url;
    private int status;
    private String message;
    private List<Violation> violations;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public ErrorResponse(List<Violation> violations) {
        this.violations = violations;
    }
}