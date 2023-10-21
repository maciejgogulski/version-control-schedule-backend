package com.maciejgogulski.eventschedulingbackend.domain;

import com.maciejgogulski.eventschedulingbackend.enums.ModificationType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Modification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private StagedEvent stagedEvent;

    @ManyToOne
    private BlockParameter blockParameter;

    private String type;

    private String oldValue;

    private String newValue;

    private LocalDateTime timestamp;
}
