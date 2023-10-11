package com.maciejgogulski.eventschedulingbackend.repositories;

import com.maciejgogulski.eventschedulingbackend.domain.Addressee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddresseeRepository extends JpaRepository<Addressee, Long> {
}
