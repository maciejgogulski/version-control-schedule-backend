package com.maciejgogulski.eventschedulingbackend.repositories;

import com.maciejgogulski.eventschedulingbackend.domain.Modification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModificationRepository extends JpaRepository<Modification, Long> {

    @Procedure
    Optional<Modification> find_modification_for_staged_event_and_parameter_dict(Long stagedEventId, Long scheduleBlockId, Long parameterDictId);
}
