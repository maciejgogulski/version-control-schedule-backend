package com.maciejgogulski.eventschedulingbackend.repositories;

import com.maciejgogulski.eventschedulingbackend.domain.ScheduleBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ScheduleBlockRepository extends JpaRepository<ScheduleBlock, Long> {
    List<ScheduleBlock> findAllByScheduleTagIdAndStartDateBetweenOrderByStartDateAsc(Long scheduleTagId, Date startOfDay, Date endOfDay);
}
