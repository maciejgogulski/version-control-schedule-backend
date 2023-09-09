package com.maciejgogulski.eventschedulingbackend.service;

import com.maciejgogulski.eventschedulingbackend.domain.ScheduleTag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ScheduleTagService {

    ScheduleTag addScheduleTag(String name);

    ScheduleTag getScheduleTag(Long scheduleTagId) throws EntityNotFoundException;

    List<ScheduleTag> getScheduleTags() throws EntityNotFoundException;

    ScheduleTag updateScheduleTag(ScheduleTag scheduleTag) throws EntityNotFoundException;

    void deleteScheduleTag(Long scheduleTagId) throws EntityNotFoundException;
}
