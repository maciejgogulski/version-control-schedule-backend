package com.maciejgogulski.eventschedulingbackend.service;

import com.maciejgogulski.eventschedulingbackend.domain.ScheduleTag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public interface ScheduleTagService {

    ScheduleTag addScheduleTag(String name);

    ScheduleTag getScheduleTag(Long scheduleTagId) throws EntityNotFoundException;
}
