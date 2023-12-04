package com.maciejgogulski.versioncontrolschedule.service;

import com.maciejgogulski.versioncontrolschedule.domain.Schedule;
import com.maciejgogulski.versioncontrolschedule.dto.ScheduleTagDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ScheduleTagService {

    Schedule addScheduleTag(String name);

    Schedule getScheduleTag(Long scheduleTagId) throws EntityNotFoundException;

    List<Schedule> getScheduleTags() throws EntityNotFoundException;

    ScheduleTagDto updateScheduleTag(ScheduleTagDto scheduleTagDto) throws EntityNotFoundException;

    void deleteScheduleTag(Long scheduleTagId) throws EntityNotFoundException;
}
