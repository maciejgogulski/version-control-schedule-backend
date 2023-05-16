package com.maciejgogulski.eventschedulingbackend.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class EventType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String messageContent;

    @ManyToMany
    @JoinTable(
            name = "event_type_addressee_group",
            joinColumns = @JoinColumn(name = "event_type_id"),
            inverseJoinColumns = @JoinColumn(name = "addresee_group_id"))
    private Set<AddresseeGroup> addresseeGroups = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "event_type_addressee",
            joinColumns = @JoinColumn(name = "event_type_id"),
            inverseJoinColumns = @JoinColumn(name = "addresee_id"))
    private Set<Addressee> addressees = new HashSet<>();


}
