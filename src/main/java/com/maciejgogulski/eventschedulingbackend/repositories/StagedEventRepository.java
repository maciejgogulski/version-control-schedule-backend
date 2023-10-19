package com.maciejgogulski.eventschedulingbackend.repositories;

import com.maciejgogulski.eventschedulingbackend.domain.StagedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StagedEventRepository extends JpaRepository<StagedEvent, Long> {
}
