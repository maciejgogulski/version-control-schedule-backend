package com.maciejgogulski.versioncontrolschedule.service.impl;

import com.maciejgogulski.versioncontrolschedule.dao.ModificationDao;
import com.maciejgogulski.versioncontrolschedule.domain.Version;
import com.maciejgogulski.versioncontrolschedule.dto.ModificationDto;
import com.maciejgogulski.versioncontrolschedule.dto.VersionDto;
import com.maciejgogulski.versioncontrolschedule.repositories.ScheduleRepository;
import com.maciejgogulski.versioncontrolschedule.repositories.VersionRepository;
import com.maciejgogulski.versioncontrolschedule.service.MessageService;
import com.maciejgogulski.versioncontrolschedule.service.VersionService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VersionServiceImpl extends CrudServiceImpl<Version, VersionDto> implements VersionService {

    private final Logger logger = LoggerFactory.getLogger(VersionServiceImpl.class);

    private final ScheduleRepository scheduleRepository;

    private final ModificationDao modificationDao;

    private final MessageService messageService;

    public VersionServiceImpl(VersionRepository versionRepository,
                              ScheduleRepository scheduleRepository,
                              ModificationDao modificationDao, MessageService messageService) {
        this.scheduleRepository = scheduleRepository;
        this.modificationDao = modificationDao;
        this.messageService = messageService;
        this.repository = versionRepository;
    }

    @Override
    protected Version convertToEntity(VersionDto dto) {
        Version version = new Version();
        version.setId(dto.id());
        version.setSchedule(
                scheduleRepository
                        .findById(dto.scheduleId())
                        .orElseThrow(EntityNotFoundException::new)
        );
        version.setTimestamp(dto.timestamp());
        version.setCommitted(dto.committed());
        return version;
    }

    @Override
    protected VersionDto convertToDto(Version entity) {
        return new VersionDto(
                entity.getId(),
                entity.getSchedule().getId(),
                entity.isCommitted(),
                entity.getTimestamp()
        );
    }

    @Override
    protected Version updateEntityFromDto(Version entity, VersionDto dto) {
        entity.setId(dto.id());
        entity.setSchedule(
                scheduleRepository
                        .findById(dto.scheduleId())
                        .orElseThrow(EntityNotFoundException::new)
        );
        entity.setCommitted(dto.committed());
        entity.setTimestamp(dto.timestamp());
        return entity;
    }

    @Override
    public List<ModificationDto> getModificationsForVersion(Long versionId) {
        return modificationDao.get_modifications_for_version(versionId);
    }

    @Transactional
    @Override
    public VersionDto getLatestVersionForSchedule(Long scheduleId) {
        logger.debug("[getLatestVersionForSchedule] Getting latest version for schedule id: " + scheduleId);
        Version version = ((VersionRepository) repository)
                .find_latest_version_for_schedule(scheduleId)
                .orElseThrow(EntityNotFoundException::new);

        return convertToDto(version);
    }

    @Transactional
    @Override
    public void commitVersion(Long versionId) throws MessagingException {
        logger.debug("[commitVersion] Committing version with id: " + versionId);
        ((VersionRepository) repository).commit_version(versionId);
        logger.debug("[commitVersion] Committed version with id: " + versionId);

        messageService.notifyAddresseesAboutModifications(versionId);

        logger.debug("[commitVersion] Creating new version.");
        Version previousVersion = repository.findById(versionId)
                .orElseThrow(EntityNotFoundException::new);

        Version version = new Version();
        version.setSchedule(previousVersion.getSchedule());
        version.setCommitted(false);
        version.setTimestamp(LocalDateTime.now());
        repository.save(version);

        logger.debug("[commitVersion] Created new version with id: " );
    }

}
