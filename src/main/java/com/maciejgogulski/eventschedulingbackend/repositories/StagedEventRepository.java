package com.maciejgogulski.eventschedulingbackend.repositories;

import com.maciejgogulski.eventschedulingbackend.domain.StagedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StagedEventRepository extends JpaRepository<StagedEvent, Long> {

    @Procedure
    Optional<StagedEvent> find_latest_staged_event_for_block_parameter(Long blockParameterId);
}
