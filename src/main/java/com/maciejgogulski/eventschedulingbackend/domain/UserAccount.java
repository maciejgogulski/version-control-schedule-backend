package com.maciejgogulski.eventschedulingbackend.domain;

import jakarta.persistence.Entity;

@Entity
public class UserAccount extends Addressee {

    private String userName;

    private String password;

}
