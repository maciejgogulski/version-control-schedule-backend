package com.maciejgogulski.versioncontrolschedule.service;

import com.maciejgogulski.versioncontrolschedule.domain.Schedule;
import com.maciejgogulski.versioncontrolschedule.dto.ScheduleDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ScheduleService {

    Schedule addSchedule(String name);

    Schedule getSchedule(Long scheduleId) throws EntityNotFoundException;

    List<Schedule> getSchedules() throws EntityNotFoundException;

    ScheduleDto updateSchedule(ScheduleDto scheduleDto) throws EntityNotFoundException;

    void deleteSchedule(Long scheduleId) throws EntityNotFoundException;
}
