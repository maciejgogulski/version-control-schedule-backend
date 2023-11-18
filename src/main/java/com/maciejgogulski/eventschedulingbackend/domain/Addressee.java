package com.maciejgogulski.eventschedulingbackend.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@SQLDelete(sql = "UPDATE addressee SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Addressee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String firstName;

    private String lastName;

    @ManyToMany(mappedBy = "addressees")
    private Set<AddresseeGroup> addresseeGroups = new HashSet<>();

    @Column(nullable = false)
    private boolean deleted;
}
