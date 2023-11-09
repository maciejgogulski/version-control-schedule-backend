package com.maciejgogulski.eventschedulingbackend.service.impl;

import com.maciejgogulski.eventschedulingbackend.domain.BlockParameter;
import com.maciejgogulski.eventschedulingbackend.domain.Modification;
import com.maciejgogulski.eventschedulingbackend.domain.StagedEvent;
import com.maciejgogulski.eventschedulingbackend.enums.ModificationType;
import com.maciejgogulski.eventschedulingbackend.repositories.ModificationRepository;
import com.maciejgogulski.eventschedulingbackend.repositories.StagedEventRepository;
import com.maciejgogulski.eventschedulingbackend.service.ModificationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ModificationServiceImpl implements ModificationService {

    private final Logger logger = LoggerFactory.getLogger(ModificationServiceImpl.class);

    private final ModificationRepository modificationRepository;

    private final StagedEventRepository stagedEventRepository;

    public ModificationServiceImpl(ModificationRepository modificationRepository, StagedEventRepository stagedEventRepository) {
        this.modificationRepository = modificationRepository;
        this.stagedEventRepository = stagedEventRepository;
    }

    @Override
    @Transactional
    public void assignParameterToScheduleBlockModification(BlockParameter blockParameter) {
        final String METHOD_NAME = "[assignParameterToScheduleBlockModification]";
        logger.info(METHOD_NAME + " Creating proper modification for creating block parameter id: " + blockParameter.getId());

        Modification modification = new Modification();
        modification.setType(String.valueOf(ModificationType.CREATE_PARAMETER));


        logger.debug(METHOD_NAME + " Searching for latest uncommitted staged event");
        StagedEvent latestUncommittedStagedEvent = stagedEventRepository.find_latest_staged_event_for_block_parameter(blockParameter.getId(), false)
                .orElseThrow(EntityNotFoundException::new);

//        logger.debug(METHOD_NAME + " Searching for latest committed staged event");
//        StagedEvent latestCommittedStagedEvent = stagedEventRepository.find_latest_staged_event_for_block_parameter(blockParameter.getId(), true)
//                .get();

        logger.debug(METHOD_NAME + " Searching for parameter modification for staged event");
        Optional<Modification> modificationOptional =
                modificationRepository.find_modification_for_staged_event_and_parameter_dict(
                        latestUncommittedStagedEvent.getId(),
                        blockParameter.getScheduleBlock().getId(),
                        blockParameter.getParameterDict().getId()
                );

        if (modificationOptional.isPresent()) {
            modification = modificationOptional.get();
            ModificationType modificationType = ModificationType.valueOf(modification.getType());
            logger.debug(METHOD_NAME + " Found modification with type " + modificationType);

            if (modificationType.equals(ModificationType.DELETE_PARAMETER)) {

                if (blockParameter.getValue().equals(modification.getOldValue())) {
                    logger.debug(METHOD_NAME + " Old value of modification is the same as newly assigned parameter value - deleting modification");
                    modificationRepository.deleteById(modification.getId());
                    return;
                } else {
                    logger.debug(METHOD_NAME + " Old value of modification is different from assigned parameter value - setting modification type to UPDATE_PARAMETER");
                    modification.setType(ModificationType.UPDATE_PARAMETER.name());
                }
            }
        }

        modification.setStagedEvent(latestUncommittedStagedEvent);
        modification.setBlockParameter(blockParameter);
        modification.setNewValue(blockParameter.getValue());
        modification.setTimestamp(LocalDateTime.now());

        modificationRepository.save(modification);

        logger.info(METHOD_NAME + " Successfully created proper modification for creating block parameter id: " + blockParameter.getId());
    }

    @Override
    @Transactional
    public void updateParameterWithinBlockModification(BlockParameter blockParameter) {
        final String METHOD_NAME = "[updateParameterWithinBlockModification]";
        logger.info(METHOD_NAME + " Creating proper modification for updating block parameter id: " + blockParameter.getId());

        Modification modification = new Modification();
        modification.setType(ModificationType.UPDATE_PARAMETER.name());

        logger.debug(METHOD_NAME + " Searching for staged event");
        StagedEvent stagedEvent = stagedEventRepository.find_latest_staged_event_for_block_parameter(blockParameter.getId(), false)
                .orElseThrow(EntityNotFoundException::new);

        logger.debug(METHOD_NAME + " Searching parameter modification for staged event");
        Optional<Modification> modificationOptional =
                modificationRepository.find_modification_for_staged_event_and_parameter_dict(
                        stagedEvent.getId(),
                        blockParameter.getScheduleBlock().getId(),
                        blockParameter.getParameterDict().getId()
                );

        if (modificationOptional.isPresent()) {
            modification = modificationOptional.get();
            ModificationType modificationType = ModificationType.valueOf(modification.getType());
            logger.debug(METHOD_NAME + " Found modification with type " + modificationType);

            switch (modificationType) {
                case CREATE_PARAMETER, UPDATE_PARAMETER -> {
                    if (blockParameter.getValue().equals(modification.getNewValue())) {
                        logger.debug(METHOD_NAME + " New value of modification is the same as updated parameter value - modification remains the same");
                        return;
                    }
                    logger.debug(METHOD_NAME + " New value of modification is different from updated parameter value - updating new value of modification");
                }
                case DELETE_PARAMETER -> {
                    if (blockParameter.getValue().equals(modification.getOldValue())) {
                        logger.debug(METHOD_NAME + " Old value of modification is the same as updated parameter value - deleting modification");
                        modificationRepository.deleteById(modification.getId());
                        return;
                    }
                    logger.debug(METHOD_NAME + " Old value of modification is different from assigned parameter value - setting modification type to UPDATE_PARAMETER");
                    modification.setType(ModificationType.UPDATE_PARAMETER.name());
                }
            }
        }
        modification.setStagedEvent(stagedEvent);
        modification.setBlockParameter(blockParameter);
        modification.setNewValue(blockParameter.getValue());
        modification.setTimestamp(LocalDateTime.now());

        modificationRepository.save(modification);

        logger.info(METHOD_NAME + " Successfully created proper modification for updating block parameter id: " + blockParameter.getId());
    }

    @Override
    @Transactional
    public void deleteParameterFromScheduleBlockModification(BlockParameter blockParameter) {
        final String METHOD_NAME = "[deleteParameterFromScheduleBlockModification]";

        Modification modification = new Modification();
        modification.setType(String.valueOf(ModificationType.DELETE_PARAMETER));

        logger.debug(METHOD_NAME + " Searching for staged event");
        StagedEvent stagedEvent = stagedEventRepository.find_latest_staged_event_for_block_parameter(blockParameter.getId(), false)
                .orElseThrow(EntityNotFoundException::new);

        logger.debug(METHOD_NAME + " Searching parameter modification for staged event");
        Optional<Modification> modificationOptional =
                modificationRepository.find_modification_for_staged_event_and_parameter_dict(
                        stagedEvent.getId(),
                        blockParameter.getScheduleBlock().getId(),
                        blockParameter.getParameterDict().getId()
                );

        if (modificationOptional.isPresent()) {
            modification = modificationOptional.get();
            ModificationType modificationType = ModificationType.valueOf(modification.getType());
            logger.debug(METHOD_NAME + " Found modification with type " + modificationType);

            switch (modificationType) {
                case CREATE_PARAMETER -> {
                    logger.debug(METHOD_NAME + " Modification creating that parameter exist - deleting modification");
                    modificationRepository.deleteById(modification.getId());
                    return;
                }
                case UPDATE_PARAMETER -> {
                    logger.debug(METHOD_NAME + " Modification updating that parameter exist - setting modification type to DELETE_PARAMETER");
                    modification.setType(ModificationType.DELETE_PARAMETER.name());
                    return;
                }
            }
        }
        modification.setStagedEvent(stagedEvent);
        modification.setBlockParameter(blockParameter);
        modification.setNewValue(blockParameter.getValue());
        modification.setTimestamp(LocalDateTime.now());

        modificationRepository.save(modification);

        logger.info(METHOD_NAME + " Successfully created proper modification for deleting block parameter id: " + blockParameter.getId());
    }
}
