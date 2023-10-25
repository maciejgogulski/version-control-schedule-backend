package com.maciejgogulski.eventschedulingbackend.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Data
@Entity
@SQLDelete(sql = "UPDATE block_parameter SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
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

    @Column(nullable = false)
    private boolean deleted;
}
