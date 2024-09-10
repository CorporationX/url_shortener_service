package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;


import java.sql.Timestamp;

public class Url {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="hash", length = 6, nullable = false, unique = true)
    String hash;
    @Column(name = "url", nullable = false, unique = true)
    String url;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    Timestamp createdAt;


}
