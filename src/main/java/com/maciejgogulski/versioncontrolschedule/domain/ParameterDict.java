package com.maciejgogulski.versioncontrolschedule.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ParameterDict {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}
