package com.maciejgogulski.eventschedulingbackend.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ScheduleTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
}
