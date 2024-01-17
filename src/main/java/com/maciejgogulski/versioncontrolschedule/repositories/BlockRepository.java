package com.maciejgogulski.versioncontrolschedule.repositories;

import com.maciejgogulski.versioncontrolschedule.domain.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    @Procedure
    List<Block> find_blocks_in_day(Long scheduleId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
