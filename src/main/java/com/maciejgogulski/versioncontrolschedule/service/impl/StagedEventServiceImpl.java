package com.maciejgogulski.versioncontrolschedule.service.impl;

import com.maciejgogulski.versioncontrolschedule.dao.ModificationDao;
import com.maciejgogulski.versioncontrolschedule.domain.Version;
import com.maciejgogulski.versioncontrolschedule.dto.ModificationDto;
import com.maciejgogulski.versioncontrolschedule.dto.StagedEventDto;
import com.maciejgogulski.versioncontrolschedule.repositories.ScheduleTagRepository;
import com.maciejgogulski.versioncontrolschedule.repositories.StagedEventRepository;
import com.maciejgogulski.versioncontrolschedule.service.MessageService;
import com.maciejgogulski.versioncontrolschedule.service.StagedEventService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StagedEventServiceImpl extends CrudServiceImpl<Version, StagedEventDto> implements StagedEventService {

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
    protected Version convertToEntity(StagedEventDto dto) {
        Version version = new Version();
        version.setId(dto.id());
        version.setSchedule(
                scheduleTagRepository
                        .findById(dto.scheduleTagId())
                        .orElseThrow(EntityNotFoundException::new)
        );
        version.setTimestamp(dto.timestamp());
        version.setCommitted(dto.committed());
        return version;
    }

    @Override
    protected StagedEventDto convertToDto(Version entity) {
        return new StagedEventDto(
                entity.getId(),
                entity.getSchedule().getId(),
                entity.isCommitted(),
                entity.getTimestamp()
        );
    }

    @Override
    protected Version updateEntityFromDto(Version entity, StagedEventDto dto) {
        entity.setId(dto.id());
        entity.setSchedule(
                scheduleTagRepository
                        .findById(dto.scheduleTagId())
                        .orElseThrow(EntityNotFoundException::new)
        );
        entity.setCommitted(dto.committed());
        entity.setTimestamp(dto.timestamp());
        return entity;
    }

    @Override
    public List<ModificationDto> getModificationsForStagedEvent(Long stagedEventId) {
        logger.debug("[getModificationsForStagedEvent] Getting modifications for staged event id: " + stagedEventId);
        List<ModificationDto> modificationDtoList = modificationDao.get_modifications_for_staged_event(stagedEventId);
        logger.debug("[getModificationsForStagedEvent] Successfully fetched " + modificationDtoList.size() + " modifications for staged event id: " + stagedEventId);
        return modificationDtoList;
    }

    @Transactional
    @Override
    public StagedEventDto getLatestStagedEventForSchedule(Long scheduleTagId) {
        logger.debug("[getLatestStagedEvent] Getting latest staged event for schedule tag id: " + scheduleTagId);
        Version version = ((StagedEventRepository) repository)
                .find_latest_staged_event_for_schedule(scheduleTagId)
                .orElseThrow(EntityNotFoundException::new);

        return convertToDto(version);
    }

    @Transactional
    @Override
    public void commitStagedEvent(Long stagedEventId) throws MessagingException {
        logger.debug("[commitStagedEvent] Committing staged event with id: " + stagedEventId);
        ((StagedEventRepository) repository).commit_staged_event(stagedEventId);
        logger.debug("[commitStagedEvent] Committed staged event with id: " + stagedEventId);

        messageService.notifyAddresseesAboutModifications(stagedEventId);

        logger.debug("[commitStagedEvent] Creating new staged event.");
        Version previousVersion = repository.findById(stagedEventId)
                .orElseThrow(EntityNotFoundException::new);

        Version version = new Version();
        version.setSchedule(previousVersion.getSchedule());
        version.setCommitted(false);
        version.setTimestamp(LocalDateTime.now());
        repository.save(version);

        logger.debug("[commitStagedEvent] Created new staged event with id: " );
    }

}
