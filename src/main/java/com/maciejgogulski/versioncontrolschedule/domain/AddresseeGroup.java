package com.maciejgogulski.versioncontrolschedule.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class AddresseeGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "addressee_group_addressee",
            joinColumns = @JoinColumn(name = "addressee_group_id"),
            inverseJoinColumns = @JoinColumn(name = "addressee_id"))
    private Set<Addressee> addressees = new HashSet<>();
}
