package com.maciejgogulski.eventschedulingbackend.service.impl;

import com.maciejgogulski.eventschedulingbackend.domain.StagedEvent;
import com.maciejgogulski.eventschedulingbackend.dto.StagedEventDto;
import com.maciejgogulski.eventschedulingbackend.repositories.ScheduleTagRepository;
import com.maciejgogulski.eventschedulingbackend.repositories.StagedEventRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class StagedEventServiceImpl extends CrudServiceImpl<StagedEvent, StagedEventDto> {

    private final ScheduleTagRepository scheduleTagRepository;

    public StagedEventServiceImpl(StagedEventRepository stagedEventRepository, ScheduleTagRepository scheduleTagRepository) {
        this.scheduleTagRepository = scheduleTagRepository;
        this.repository = stagedEventRepository;
    }

    @Override
    protected StagedEvent convertToEntity(StagedEventDto dto) {
        StagedEvent stagedEvent = new StagedEvent();
        stagedEvent.setId(dto.id());
        stagedEvent.setScheduleTag(
                scheduleTagRepository
                        .findById(dto.scheduleTagId())
                        .orElseThrow(EntityNotFoundException::new)
        );
        stagedEvent.setTimestamp(dto.timestamp());
        stagedEvent.setCommitted(dto.committed());
        return stagedEvent;
    }

    @Override
    protected StagedEventDto convertToDto(StagedEvent entity) {
        return new StagedEventDto(
                entity.getId(),
                entity.getScheduleTag().getId(),
                entity.isCommitted(),
                entity.getTimestamp()
        );
    }

    @Override
    protected StagedEvent updateEntityFromDto(StagedEvent entity, StagedEventDto dto) {
        entity.setId(dto.id());
        entity.setScheduleTag(
                scheduleTagRepository
                        .findById(dto.scheduleTagId())
                        .orElseThrow(EntityNotFoundException::new)
        );
        entity.setCommitted(dto.committed());
        entity.setTimestamp(dto.timestamp());
        return entity;
    }
}
