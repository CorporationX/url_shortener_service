package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.exception.DataValidationException;

public class UrlValidator {

    String urlRegexp = "^(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$";

    public void validateUrl(String verifiableUrl) {
        if (verifiableUrl == null ||
                verifiableUrl.isEmpty() ||
                !urlRegexp.matches(verifiableUrl)) {
            throw new DataValidationException("This url not validation");
        }
    }
}
