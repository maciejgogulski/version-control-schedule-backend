package com.maciejgogulski.eventschedulingbackend.service.impl;

import com.maciejgogulski.eventschedulingbackend.domain.BlockParameter;
import com.maciejgogulski.eventschedulingbackend.domain.Modification;
import com.maciejgogulski.eventschedulingbackend.domain.StagedEvent;
import com.maciejgogulski.eventschedulingbackend.dto.ModificationDto;
import com.maciejgogulski.eventschedulingbackend.dto.StagedEventDto;
import com.maciejgogulski.eventschedulingbackend.enums.ModificationType;
import com.maciejgogulski.eventschedulingbackend.repositories.BlockParameterRepository;
import com.maciejgogulski.eventschedulingbackend.repositories.ModificationRepository;
import com.maciejgogulski.eventschedulingbackend.repositories.ScheduleTagRepository;
import com.maciejgogulski.eventschedulingbackend.repositories.StagedEventRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StagedEventServiceImpl extends CrudServiceImpl<StagedEvent, StagedEventDto> {

    private final Logger logger = LoggerFactory.getLogger(StagedEventServiceImpl.class);

    private final ScheduleTagRepository scheduleTagRepository;

    private final ModificationRepository modificationRepository;

    private final BlockParameterRepository blockParameterRepository;

    public StagedEventServiceImpl(StagedEventRepository stagedEventRepository,
                                  ScheduleTagRepository scheduleTagRepository,
                                  ModificationRepository modificationRepository,
                                  BlockParameterRepository blockParameterRepository) {
        this.scheduleTagRepository = scheduleTagRepository;
        this.modificationRepository = modificationRepository;
        this.blockParameterRepository = blockParameterRepository;
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

    public void addModification(ModificationDto modificationDto) {
        logger.debug("[addModification] Adding new modification referring to block parameter pivot id: " + modificationDto.blockParameterId());

        Modification modification = new Modification();
        modification.setStagedEvent(
                repository
                        .findById(modificationDto.stagedEventId())
                        .orElseThrow(EntityNotFoundException::new)
        );

        BlockParameter blockParameter = blockParameterRepository
                .findById(modificationDto.blockParameterId())
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue(modificationDto.newValue());
        blockParameterRepository.save(blockParameter);

        modification.setBlockParameter(
                blockParameter
        );

        modification.setType(modificationDto.type());
        modification.setOldValue(modificationDto.oldValue());
        modification.setNewValue(modificationDto.newValue());
        modification.setTimestamp(LocalDateTime.now());

        modificationRepository.save(modification);
        logger.debug("[addModification] Successfully added new modification referring to block parameter pivot id: " + modificationDto.blockParameterId());
    }
}
