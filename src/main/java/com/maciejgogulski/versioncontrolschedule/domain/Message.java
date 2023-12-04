package com.maciejgogulski.versioncontrolschedule.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date sendAt;

    private String content;

    @ManyToOne
    private Addressee addressee;

    @ManyToOne
    private Version version;
}
