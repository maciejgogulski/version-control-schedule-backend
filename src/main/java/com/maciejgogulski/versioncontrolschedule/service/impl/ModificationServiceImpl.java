package com.maciejgogulski.versioncontrolschedule.service.impl;

import com.maciejgogulski.versioncontrolschedule.domain.BlockParameter;
import com.maciejgogulski.versioncontrolschedule.domain.Modification;
import com.maciejgogulski.versioncontrolschedule.domain.Version;
import com.maciejgogulski.versioncontrolschedule.enums.ModificationType;
import com.maciejgogulski.versioncontrolschedule.repositories.ModificationRepository;
import com.maciejgogulski.versioncontrolschedule.repositories.StagedEventRepository;
import com.maciejgogulski.versioncontrolschedule.service.ModificationService;
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
        Version latestUncommittedVersion = stagedEventRepository.find_latest_staged_event_for_block_parameter(blockParameter.getId(), false)
                .orElseThrow(EntityNotFoundException::new);

        logger.debug(METHOD_NAME + " Searching for parameter modification for staged event");
        Optional<Modification> modificationOptional =
                modificationRepository.find_modification_for_staged_event_and_parameter_dict(
                        latestUncommittedVersion.getId(),
                        blockParameter.getBlock().getId(),
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

        modification.setVersion(latestUncommittedVersion);
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

        logger.debug(METHOD_NAME + " Searching for uncommitted staged event");
        Version version = stagedEventRepository.find_latest_staged_event_for_block_parameter(blockParameter.getId(), false)
                .orElseThrow(EntityNotFoundException::new);

        logger.debug(METHOD_NAME + " Searching parameter modification for staged event");
        Optional<Modification> modificationOptional =
                modificationRepository.find_modification_for_staged_event_and_parameter_dict(
                        version.getId(),
                        blockParameter.getBlock().getId(),
                        blockParameter.getParameterDict().getId()
                );

        if (modificationOptional.isPresent()) {
            modification = modificationOptional.get();
            ModificationType modificationType = ModificationType.valueOf(modification.getType());
            logger.debug(METHOD_NAME + " Found modification with type " + modificationType);

            switch (modificationType) {
                case CREATE_PARAMETER -> {
                    if (blockParameter.getValue().equals(modification.getNewValue())) {
                        logger.debug(METHOD_NAME + " New value of modification is the same as updated parameter value - modification remains the same");
                        return;
                    }
                    logger.debug(METHOD_NAME + " New value of modification is different from updated parameter value - updating new value of modification");
                }
                case UPDATE_PARAMETER -> {
                    if (blockParameter.getValue().equals(modification.getNewValue())) {
                        logger.debug(METHOD_NAME + " New value of modification is the same as updated parameter value - modification remains the same");
                        return;
                    }
                    if (blockParameter.getValue().equals(modification.getOldValue())) {
                        logger.debug(METHOD_NAME + " Old value of modification is the same as updated parameter value - deleting modification");
                        modificationRepository.deleteById(modification.getId());
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
        modification.setVersion(version);
        modification.setBlockParameter(blockParameter);
        modification.setNewValue(blockParameter.getValue());
        modification.setTimestamp(LocalDateTime.now());

        if (modification.getType().equals(ModificationType.UPDATE_PARAMETER.name())) {
            logger.debug(METHOD_NAME + " Setting old value of modification");
            logger.debug(METHOD_NAME + " Searching for committed staged event");

            version = stagedEventRepository.find_latest_staged_event_for_block_parameter(blockParameter.getId(), true)
                    .orElseThrow(EntityNotFoundException::new);

            logger.debug(METHOD_NAME + " Searching for previous modification");
            Modification previousModification = modificationRepository.find_modification_for_staged_event_and_parameter_dict(
                            version.getId(),
                            blockParameter.getBlock().getId(),
                            blockParameter.getParameterDict().getId()
                    )
                    .orElseThrow(EntityNotFoundException::new);

            if (!(previousModification.getType().equals(ModificationType.UPDATE_PARAMETER.name())
                    || previousModification.getType().equals(ModificationType.CREATE_PARAMETER.name())
            )) throw new IllegalArgumentException("Previous modification can't be DELETE_PARAMETER");

            if (previousModification.getNewValue() == null)
                throw new IllegalArgumentException("Previous update modification must have the new value");

            if (modification.getNewValue().equals(previousModification.getNewValue())) {
                logger.info(METHOD_NAME + " Previous modifications new value is the same as the new value");
                return;
            }

            modification.setOldValue(previousModification.getNewValue());
        }

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
        Version version = stagedEventRepository.find_latest_staged_event_for_block_parameter(blockParameter.getId(), false)
                .orElseThrow(EntityNotFoundException::new);

        logger.debug(METHOD_NAME + " Searching parameter modification for staged event");
        Optional<Modification> modificationOptional =
                modificationRepository.find_modification_for_staged_event_and_parameter_dict(
                        version.getId(),
                        blockParameter.getBlock().getId(),
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
        modification.setVersion(version);
        modification.setBlockParameter(blockParameter);
        modification.setOldValue(blockParameter.getValue());
        modification.setTimestamp(LocalDateTime.now());

        modificationRepository.save(modification);

        logger.info(METHOD_NAME + " Successfully created proper modification for deleting block parameter id: " + blockParameter.getId());
    }
}
