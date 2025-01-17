package faang.school.urlshortenerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String error;
    private String message;
//    private List<String> details;
}