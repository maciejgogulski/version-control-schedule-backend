package com.maciejgogulski.eventschedulingbackend.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class BlockParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ParameterDict parameterDict;

    @ManyToOne
    private ScheduleBlock scheduleBlock;

    @Column(nullable = false)
    private String value;

}
