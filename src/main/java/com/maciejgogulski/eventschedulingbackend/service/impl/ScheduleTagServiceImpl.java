package com.maciejgogulski.eventschedulingbackend.service.impl;

import com.maciejgogulski.eventschedulingbackend.domain.ScheduleTag;
import com.maciejgogulski.eventschedulingbackend.repositories.ScheduleTagRepository;
import com.maciejgogulski.eventschedulingbackend.service.ScheduleTagService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ScheduleTagServiceImpl implements ScheduleTagService {

    @Autowired
    ScheduleTagRepository scheduleTagRepository;

    @Override
    public ScheduleTag addScheduleTag(String name) {
        ScheduleTag scheduleTag = new ScheduleTag();
        scheduleTag.setName(name);
        return scheduleTagRepository.save(scheduleTag);
    }

    @Override
    public ScheduleTag getScheduleTag(Long scheduleTagId) throws EntityNotFoundException{
        Optional<ScheduleTag> scheduleTag = scheduleTagRepository.findById(scheduleTagId);

        if (scheduleTag.isPresent()) {
            return scheduleTag.get();
        } else {
            throw new EntityNotFoundException();
        }
    }
}
