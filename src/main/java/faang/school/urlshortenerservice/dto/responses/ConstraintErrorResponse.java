package faang.school.urlshortenerservice.dto.responses;

import java.util.List;

public record ConstraintErrorResponse(List<Violation> violations) {
}
