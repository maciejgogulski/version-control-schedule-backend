package com.maciejgogulski.eventschedulingbackend.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class StagedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private ScheduleTag scheduleTag;

    private boolean committed;

    private LocalDateTime timestamp;

    @Override
    public String toString() {
        return """
                {
                    "id": "%s",
                    "committed" : "%s",
                    "timestamp": "%s"
                }
                """.formatted(id.toString(), committed, timestamp.toString());
    }
}
