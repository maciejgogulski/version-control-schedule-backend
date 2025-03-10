package com.maciejgogulski.versioncontrolschedule.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@SQLDelete(sql = "UPDATE block SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Block implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Schedule schedule;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Override
    public String toString() {
        return """
                {
                    "id": "%s",
                    "name" : "%s",
                    "startDate": "%s",
                    "endDate": "%s"
                }
                """.formatted(id.toString(), name, startDate.toString(), endDate.toString());
    }
}
