package faang.school.urlshortenerservice.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "errorDate",
        "errorMessage",
        "detail"
})
public class ErrorResponse {
    @JsonProperty("errorDate")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private LocalDateTime errorDate;

    @JsonProperty("errorMessage")
    private String errorMessage;
    private Map<String, String> detail;

    public ErrorResponse(String errorMessage) {
        this.errorDate = LocalDateTime.now();
        this.errorMessage = errorMessage;
    }

    public ErrorResponse(String errorMessage, Map<String, String> detail) {
        this.errorDate = LocalDateTime.now();
        this.errorMessage = errorMessage;
        this.detail = detail;
    }
}
