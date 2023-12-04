package com.maciejgogulski.versioncontrolschedule.service.impl;

import com.maciejgogulski.versioncontrolschedule.domain.Schedule;
import com.maciejgogulski.versioncontrolschedule.dto.ScheduleTagDto;
import com.maciejgogulski.versioncontrolschedule.dto.StagedEventDto;
import com.maciejgogulski.versioncontrolschedule.repositories.ScheduleTagRepository;
import com.maciejgogulski.versioncontrolschedule.service.ScheduleTagService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleTagServiceImpl implements ScheduleTagService {

    private final Logger logger = LoggerFactory.getLogger(ScheduleTagServiceImpl.class);

    private final ScheduleTagRepository scheduleTagRepository;

    private final StagedEventServiceImpl stagedEventService;

    public ScheduleTagServiceImpl(ScheduleTagRepository scheduleTagRepository, StagedEventServiceImpl stagedEventService) {
        this.scheduleTagRepository = scheduleTagRepository;
        this.stagedEventService = stagedEventService;
    }

    @Override
    public Schedule addScheduleTag(String name) {
        logger.debug("[addScheduleTag] Creating schedule tag with name: " + name);
        Schedule schedule = new Schedule();
        schedule.setName(name);
        schedule = scheduleTagRepository.save(schedule);
        logger.debug("[addScheduleTag] Successfully created schedule tag with name: " + name);

        stagedEventService.create(new StagedEventDto(
                null,
                schedule.getId(),
                false,
                LocalDateTime.now()
        ));

        return schedule;
    }

    @Override
    public Schedule getScheduleTag(Long scheduleTagId) throws EntityNotFoundException {
        logger.debug("[getScheduleTag] Fetching schedule tag with id: " + scheduleTagId);

        Optional<Schedule> scheduleTag = scheduleTagRepository.findById(scheduleTagId);

        if (scheduleTag.isPresent()) {
            logger.debug("[getScheduleTag] Successfully fetched schedule tag with id: " + scheduleTagId);
            return scheduleTag.get();
        } else {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public List<Schedule> getScheduleTags() {
        logger.debug("[getScheduleTags] Fetching all schedule tags");
        List<Schedule> schedules = scheduleTagRepository.findAll();
        logger.debug("[getScheduleTags] Successfully fetched " + schedules.size() + " schedule tags");
        return schedules;
    }

    @Override
    public ScheduleTagDto updateScheduleTag(ScheduleTagDto scheduleTagDto) throws EntityNotFoundException {
        logger.debug("[updateScheduleTag] Updating schedule tag with id: " + scheduleTagDto.id());
        Optional<Schedule> scheduleTag = scheduleTagRepository.findById(scheduleTagDto.id());

        if (scheduleTag.isPresent()) {
            Schedule fetchedSchedule = scheduleTag.get();
            fetchedSchedule.setName(scheduleTagDto.name());
            scheduleTagRepository.save(fetchedSchedule);
            logger.debug("[updateScheduleTag] Successfully updated schedule tag with id: " + scheduleTagDto.id());
            return scheduleTagDto;
        } else {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public void deleteScheduleTag(Long scheduleTagId) throws EntityNotFoundException {
        logger.debug("[deleteScheduleTag] Deleting schedule tag with id: " + scheduleTagId);

        Optional<Schedule> scheduleTag = scheduleTagRepository.findById(scheduleTagId);

        if (scheduleTag.isPresent()) {
            scheduleTagRepository.delete(scheduleTag.get());
            logger.debug("[deleteScheduleTag] Successfully deleted schedule tag with id: " + scheduleTagId);
        } else {
            throw new EntityNotFoundException();
        }
    }
}
