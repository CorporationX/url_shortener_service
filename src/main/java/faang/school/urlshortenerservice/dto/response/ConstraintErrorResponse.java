package faang.school.urlshortenerservice.dto.response;

import faang.school.urlshortenerservice.exception.Violation;

import java.util.List;

public record ConstraintErrorResponse(List<Violation> violations) {
}
