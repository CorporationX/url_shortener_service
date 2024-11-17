package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "url")
public class Url {

    @Id
    @NotNull
    @Size(min = 1, max = 6)
    private String hash;

    @NotNull
    @Size(min = 1, max = 4096)
    private String url;

    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
