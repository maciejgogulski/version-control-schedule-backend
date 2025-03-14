package com.maciejgogulski.versioncontrolschedule.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.List;

@Data
@Entity
@SQLDelete(sql = "UPDATE block_parameter SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class BlockParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private ParameterDict parameterDict;


    @ManyToOne(cascade = CascadeType.PERSIST)
    private Block block;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<Modification> modifications;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private boolean deleted;

    @Override
    public String toString() {
        return id.toString() + ": " + value;
    }
}
