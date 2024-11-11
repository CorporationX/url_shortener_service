package faang.school.urlshortenerservice.entity;

import faang.school.urlshortenerservice.dto.ValidationErrorDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String serviceName;
    private String globalMessage;
    private List<ValidationErrorDto> fieldErrors;
    private int status;
}