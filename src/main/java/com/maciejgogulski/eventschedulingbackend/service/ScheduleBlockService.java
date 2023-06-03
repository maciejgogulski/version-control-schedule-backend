package com.maciejgogulski.eventschedulingbackend.service;

import com.maciejgogulski.eventschedulingbackend.domain.ScheduleBlock;
import org.springframework.stereotype.Service;

@Service
public interface ScheduleBlockService {

    ScheduleBlock addScheduleBlock(ScheduleBlock scheduleBlock);

    ScheduleBlock getScheduleBlock(Long scheduleBlockId);

    ScheduleBlock updateScheduleBlock(ScheduleBlock scheduleBlock);

    void deleteScheduleBlock(Long scheduleBlockId);
}
