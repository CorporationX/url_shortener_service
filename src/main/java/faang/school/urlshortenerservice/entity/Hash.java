package faang.school.urlshortenerservice.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "hash")
public class Hash {

    @Id
    @Column(length = 6, nullable = false, unique = true)
    private String hash;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;
}

