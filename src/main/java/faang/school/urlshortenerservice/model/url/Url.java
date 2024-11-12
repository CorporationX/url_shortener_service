package faang.school.urlshortenerservice.model.url;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "url")
public class Url {

    @Id
    @Column(name = "hash", nullable = false, length = 6)
    @Size(min = 1, max = 6)
    private String hash;

    @Column(name = "url", nullable = false, unique = true, length = 4096)
    @Size(max = 4096)
    @Pattern(regexp =
            "^(https?://)?(www\\.)?[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}(/[a-zA-Z0-9-._~:/?#@!$&'()*+,;=]*)?$")
    private String url;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "Url{"
                + "hash='" + hash + '\''
                + ", url='" + url + '\''
                + ", createdAt=" + createdAt
                + '}';
    }
}
