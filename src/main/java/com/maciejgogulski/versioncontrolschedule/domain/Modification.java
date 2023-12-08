package com.maciejgogulski.versioncontrolschedule.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Modification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Version version;

    @ManyToOne
    private BlockParameter blockParameter;

    private String type;

    private String oldValue;

    private String newValue;

    private LocalDateTime timestamp;
}
