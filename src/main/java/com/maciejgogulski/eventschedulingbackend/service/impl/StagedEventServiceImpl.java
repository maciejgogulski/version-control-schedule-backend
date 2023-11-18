package com.maciejgogulski.eventschedulingbackend.service.impl;

import com.maciejgogulski.eventschedulingbackend.dao.ModificationDao;
import com.maciejgogulski.eventschedulingbackend.domain.StagedEvent;
import com.maciejgogulski.eventschedulingbackend.dto.ModificationDto;
import com.maciejgogulski.eventschedulingbackend.dto.StagedEventDto;
import com.maciejgogulski.eventschedulingbackend.repositories.ScheduleTagRepository;
import com.maciejgogulski.eventschedulingbackend.repositories.StagedEventRepository;
import com.maciejgogulski.eventschedulingbackend.service.MessageService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StagedEventServiceImpl extends CrudServiceImpl<StagedEvent, StagedEventDto> {

    private final Logger logger = LoggerFactory.getLogger(StagedEventServiceImpl.class);

    private final ScheduleTagRepository scheduleTagRepository;

    private final ModificationDao modificationDao;

    private final MessageService messageService;

    public StagedEventServiceImpl(StagedEventRepository stagedEventRepository,
                                  ScheduleTagRepository scheduleTagRepository,
                                  ModificationDao modificationDao, MessageService messageService) {
        this.scheduleTagRepository = scheduleTagRepository;
        this.modificationDao = modificationDao;
        this.messageService = messageService;
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

    public List<ModificationDto> getModificationsForStagedEvent(Long stagedEventId) {
        logger.debug("[getModificationsForStagedEvent] Getting modifications for staged event id: " + stagedEventId);
        List<ModificationDto> modificationDtoList = modificationDao.get_modifications_for_staged_event(stagedEventId);
        logger.debug("[getModificationsForStagedEvent] Successfully fetched " + modificationDtoList.size() + " modifications for staged event id: " + stagedEventId);
        return modificationDtoList;
    }

    @Transactional
    public StagedEventDto getLatestStagedEventForSchedule(Long scheduleTagId) {
        logger.debug("[getLatestStagedEvent] Getting latest staged event for schedule tag id: " + scheduleTagId);
        StagedEvent stagedEvent = ((StagedEventRepository) repository)
                .find_latest_staged_event_for_schedule(scheduleTagId)
                .orElseThrow(EntityNotFoundException::new);

        return convertToDto(stagedEvent);
    }

    @Transactional
    public void commitStagedEvent(Long stagedEventId) throws MessagingException {
        logger.debug("[commitStagedEvent] Committing staged event with id: " + stagedEventId);
        ((StagedEventRepository) repository).commit_staged_event(stagedEventId);
        logger.debug("[commitStagedEvent] Committed staged event with id: " + stagedEventId);

        messageService.notifyAddresseesAboutModifications(stagedEventId);

        logger.debug("[commitStagedEvent] Creating new staged event.");
        StagedEvent previousStagedEvent = repository.findById(stagedEventId)
                .orElseThrow(EntityNotFoundException::new);

        StagedEvent stagedEvent = new StagedEvent();
        stagedEvent.setScheduleTag(previousStagedEvent.getScheduleTag());
        stagedEvent.setCommitted(false);
        stagedEvent.setTimestamp(LocalDateTime.now());
        repository.save(stagedEvent);

        logger.debug("[commitStagedEvent] Created new staged event with id: " );
    }

}
