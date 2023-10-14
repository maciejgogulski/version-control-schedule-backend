package com.maciejgogulski.eventschedulingbackend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class ScheduleTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "scheduleTag", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ScheduleBlock> scheduleBlocks;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "schedule_tag_addressee",
            joinColumns = @JoinColumn(name = "schedule_tag_id"),
            inverseJoinColumns = @JoinColumn(name = "addressee_id"))
    private Set<Addressee> addressees = new HashSet<>();
}
