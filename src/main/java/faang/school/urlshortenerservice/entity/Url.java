package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "url")
public class Url {

    @Id
    @NotNull
    private String hash;

    @NotNull
    private String url;

    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
