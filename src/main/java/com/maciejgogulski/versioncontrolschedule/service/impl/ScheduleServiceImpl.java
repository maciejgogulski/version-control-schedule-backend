package com.maciejgogulski.versioncontrolschedule.service.impl;

import com.maciejgogulski.versioncontrolschedule.domain.Schedule;
import com.maciejgogulski.versioncontrolschedule.domain.Version;
import com.maciejgogulski.versioncontrolschedule.dto.ScheduleDto;
import com.maciejgogulski.versioncontrolschedule.dto.VersionDto;
import com.maciejgogulski.versioncontrolschedule.repositories.ScheduleRepository;
import com.maciejgogulski.versioncontrolschedule.service.CrudService;
import com.maciejgogulski.versioncontrolschedule.service.ScheduleService;
import com.maciejgogulski.versioncontrolschedule.service.VersionService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final Logger logger = LoggerFactory.getLogger(ScheduleServiceImpl.class);

    private final ScheduleRepository scheduleRepository;

    private final VersionService versionService;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, VersionServiceImpl versionService) {
        this.scheduleRepository = scheduleRepository;
        this.versionService = versionService;
    }

    @Override
    public Schedule addSchedule(String name) {
        logger.debug("[addSchedule] Creating schedule with name: " + name);
        Schedule schedule = new Schedule();
        schedule.setName(name);
        schedule = scheduleRepository.save(schedule);
        logger.debug("[addSchedule] Successfully created schedule with name: " + name);

        ((CrudService) versionService).create(new VersionDto(
                null,
                schedule.getId(),
                false,
                LocalDateTime.now()
        ));

        return schedule;
    }

    @Override
    public Schedule getSchedule(Long scheduleId) throws EntityNotFoundException {
        Optional<Schedule> schedule = scheduleRepository.findById(scheduleId);

        if (schedule.isPresent()) {
            return schedule.get();
        } else {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public List<Schedule> getSchedules() {
        return scheduleRepository.findAll();
    }

    @Override
    public ScheduleDto updateSchedule(ScheduleDto scheduleDto) throws EntityNotFoundException {
        logger.debug("[updateSchedule] Updating schedule with id: " + scheduleDto.id());
        Optional<Schedule> schedule = scheduleRepository.findById(scheduleDto.id());

        if (schedule.isPresent()) {
            Schedule fetchedSchedule = schedule.get();
            fetchedSchedule.setName(scheduleDto.name());
            scheduleRepository.save(fetchedSchedule);
            logger.debug("[updateSchedule] Successfully updated schedule with id: " + scheduleDto.id());
            return scheduleDto;
        } else {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public void deleteSchedule(Long scheduleId) throws EntityNotFoundException {
        logger.debug("[deleteSchedule] Deleting schedule with id: " + scheduleId);

        Optional<Schedule> schedule = scheduleRepository.findById(scheduleId);

        if (schedule.isPresent()) {
            scheduleRepository.delete(schedule.get());
            logger.debug("[deleteSchedule] Successfully deleted schedule with id: " + scheduleId);
        } else {
            throw new EntityNotFoundException();
        }
    }
}
