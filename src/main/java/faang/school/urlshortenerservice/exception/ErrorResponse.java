package faang.school.urlshortenerservice.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ErrorResponse {
  private int status;
  private String error;
  private Object message;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime timestamp = LocalDateTime.now();

  public ErrorResponse(HttpStatus status, String error, Object message) {
    this.status = status.value();
    this.error = error;
    this.message = message;
  }
}
