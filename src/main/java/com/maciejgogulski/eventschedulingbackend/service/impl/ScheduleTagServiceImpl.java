package com.maciejgogulski.eventschedulingbackend.service.impl;

import com.maciejgogulski.eventschedulingbackend.domain.ScheduleTag;
import com.maciejgogulski.eventschedulingbackend.repositories.ScheduleTagRepository;
import com.maciejgogulski.eventschedulingbackend.service.ScheduleTagService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScheduleTagServiceImpl implements ScheduleTagService {

    private final Logger logger = LoggerFactory.getLogger(ScheduleTagServiceImpl.class);

    private final ScheduleTagRepository scheduleTagRepository;

    public ScheduleTagServiceImpl(ScheduleTagRepository scheduleTagRepository) {
        this.scheduleTagRepository = scheduleTagRepository;
    }

    @Override
    public ScheduleTag addScheduleTag(String name) {
        logger.debug("[addScheduleTag] Creating schedule tag with name: " + name);
        ScheduleTag scheduleTag = new ScheduleTag();
        scheduleTag.setName(name);
        scheduleTag = scheduleTagRepository.save(scheduleTag);
        logger.debug("[addScheduleTag] Successfully created schedule tag with name: " + name);
        return scheduleTag;
    }

    @Override
    public ScheduleTag getScheduleTag(Long scheduleTagId) throws EntityNotFoundException {
        logger.debug("[getScheduleTag] Fetching schedule tag with id: " + scheduleTagId);

        Optional<ScheduleTag> scheduleTag = scheduleTagRepository.findById(scheduleTagId);

        if (scheduleTag.isPresent()) {
            logger.debug("[getScheduleTag] Successfully fetched schedule tag with id: " + scheduleTagId);
            return scheduleTag.get();
        } else {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public List<ScheduleTag> getScheduleTags() {
        logger.debug("[getScheduleTags] Fetching all schedule tags");
        List<ScheduleTag> scheduleTags = scheduleTagRepository.findAll();
        logger.debug("[getScheduleTags] Successfully fetched " + scheduleTags.size() + " schedule tags");
        return scheduleTags;
    }

    @Override
    public ScheduleTag updateScheduleTag(ScheduleTag scheduleTag) throws EntityNotFoundException {
        logger.debug("[updateScheduleTag] Updating schedule tag with id: " + scheduleTag.getId());
        if (scheduleTagRepository.findById(scheduleTag.getId()).isPresent()) {
            scheduleTag = scheduleTagRepository.save(scheduleTag);
            logger.debug("[updateScheduleTag] Successfully updated schedule tag with id: " + scheduleTag.getId());
            return scheduleTag;
        } else {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public void deleteScheduleTag(Long scheduleTagId) throws EntityNotFoundException {
        logger.debug("[deleteScheduleTag] Deleting schedule tag with id: " + scheduleTagId);

        Optional<ScheduleTag> scheduleTag = scheduleTagRepository.findById(scheduleTagId);

        if (scheduleTag.isPresent()) {
            scheduleTagRepository.delete(scheduleTag.get());
            logger.debug("[deleteScheduleTag] Successfully deleted schedule tag with id: " + scheduleTagId);
        } else {
            throw new EntityNotFoundException();
        }
    }
}
