package com.maciejgogulski.versioncontrolschedule.repositories;

import com.maciejgogulski.versioncontrolschedule.domain.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StagedEventRepository extends JpaRepository<Version, Long> {

    @Procedure
    Optional<Version> find_latest_staged_event_for_block_parameter(Long blockParameterId, boolean committed);

    @Procedure
    Optional<Version> find_latest_staged_event_for_schedule(Long scheduleTagId);

    @Procedure
    void commit_staged_event(Long stagedEventId);
}
