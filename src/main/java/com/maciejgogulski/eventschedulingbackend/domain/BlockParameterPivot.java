package com.maciejgogulski.eventschedulingbackend.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class BlockParameterPivot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private BlockParameter blockParameter;

    @ManyToOne
    private ScheduleBlock scheduleBlock;

}
