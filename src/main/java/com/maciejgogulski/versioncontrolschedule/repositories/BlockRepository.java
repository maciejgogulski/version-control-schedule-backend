package com.maciejgogulski.versioncontrolschedule.repositories;

import com.maciejgogulski.versioncontrolschedule.domain.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    List<Block> findAllByScheduleIdAndStartDateBetweenOrderByStartDateAsc(Long scheduleId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
