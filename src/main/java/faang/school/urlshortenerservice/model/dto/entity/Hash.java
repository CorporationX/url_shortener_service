package faang.school.urlshortenerservice.model.dto.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "hash")
public class Hash {

    @Id
    @Column(name = "hash", unique = true, nullable = false)
    private String hash;

    @OneToOne(mappedBy = "hash")
    Url url;
}
