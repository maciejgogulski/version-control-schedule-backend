package com.maciejgogulski.eventschedulingbackend.domain;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class Modification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date timestamp;

    private String previousValue;

    private String currentValue;

    @ManyToOne
    private BlockParameterPivot blockParameterPivot;

    @ManyToOne
    private Event event;
}
