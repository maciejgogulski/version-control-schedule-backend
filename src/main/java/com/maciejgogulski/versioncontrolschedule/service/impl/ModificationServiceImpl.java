package com.maciejgogulski.versioncontrolschedule.service.impl;

import com.maciejgogulski.versioncontrolschedule.dao.ModificationDao;
import com.maciejgogulski.versioncontrolschedule.domain.BlockParameter;
import com.maciejgogulski.versioncontrolschedule.domain.Modification;
import com.maciejgogulski.versioncontrolschedule.domain.Version;
import com.maciejgogulski.versioncontrolschedule.enums.ModificationType;
import com.maciejgogulski.versioncontrolschedule.repositories.ModificationRepository;
import com.maciejgogulski.versioncontrolschedule.repositories.VersionRepository;
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

    private final ModificationDao modificationDao;

    private final VersionRepository versionRepository;

    public ModificationServiceImpl(ModificationRepository modificationRepository, ModificationDao modificationDao, VersionRepository versionRepository) {
        this.modificationRepository = modificationRepository;
        this.modificationDao = modificationDao;
        this.versionRepository = versionRepository;
    }

    @Override
    @Transactional
    public void assignParameterToScheduleModification(BlockParameter blockParameter) {
        final String METHOD_NAME = "[assignParameterToScheduleModification]";
        logger.info(METHOD_NAME + " Creating proper modification for creating block parameter id: " + blockParameter.getId());

        logger.debug(METHOD_NAME + " Searching for modification with latest version");
        Optional<Modification> modificationOptional =
                modificationDao.find_modification_for_parameter_dict_with_latest_version(
                        blockParameter.getBlock().getId(),
                        blockParameter.getParameterDict().getId()
                );

        Modification modification;
        if (modificationOptional.isPresent()) {
            modification = modificationOptional.get();

            ModificationType modificationType = ModificationType.valueOf(modification.getType());
            logger.debug(METHOD_NAME + " Found modification with type " + modificationType);

            if (modification.getVersion().isCommitted()) {
                switch (modificationType) {
                    case CREATE_PARAMETER, UPDATE_PARAMETER -> throw new IllegalStateException(
                            "When assigning parameter, previous version's modification can only be DELETE_PARAMETER"
                    );
                    case DELETE_PARAMETER -> {
                        Modification newModification = new Modification();
                        newModification.setVersion(versionRepository.find_latest_version_for_block_parameter(
                                        blockParameter.getId(),
                                        false)
                                .orElseThrow(EntityNotFoundException::new));
                        newModification.setBlockParameter(blockParameter);
                        newModification.setOldValue(null);
                        newModification.setNewValue(blockParameter.getValue());
                        newModification.setTimestamp(LocalDateTime.now());
                        newModification.setType(ModificationType.CREATE_PARAMETER.name());

                        modification = newModification;
                    }
                }
            } else {
                if (modificationType.equals(ModificationType.DELETE_PARAMETER)) {
                    if (blockParameter.getValue().equals(modification.getOldValue())) {
                        logger.debug(METHOD_NAME + " Old value of modification is the same as newly assigned parameter value - deleting modification");
                        modificationRepository.deleteById(modification.getId());
                        return;
                    } else {
                        logger.debug(METHOD_NAME + " Old value of modification is different from assigned parameter value - setting modification type to UPDATE_PARAMETER");
                        modification.setType(ModificationType.UPDATE_PARAMETER.name());
                        modification.setNewValue(blockParameter.getValue());
                        modification.setTimestamp(LocalDateTime.now());
                    }
                }
            }
        } else {
            Modification newModification = new Modification();
            newModification.setVersion(versionRepository.find_latest_version_for_block_parameter(
                            blockParameter.getId(),
                            false)
                    .orElseThrow(EntityNotFoundException::new));
            newModification.setBlockParameter(blockParameter);
            newModification.setOldValue(null);
            newModification.setNewValue(blockParameter.getValue());
            newModification.setTimestamp(LocalDateTime.now());
            newModification.setType(ModificationType.CREATE_PARAMETER.name());

            modification = newModification;
        }

        modificationRepository.save(modification);

        logger.info(METHOD_NAME + " Successfully created proper modification for creating block parameter id: " + blockParameter.getId());
    }

    @Override
    @Transactional
    public void updateParameterWithinBlockModification(BlockParameter blockParameter) {
        final String METHOD_NAME = "[updateParameterWithinBlockModification]";
        logger.info(METHOD_NAME + " Creating proper modification for updating block parameter id: " + blockParameter.getId());

        logger.debug(METHOD_NAME + " Searching for modification with latest version");
        Modification modification =
                modificationDao.find_modification_for_parameter_dict_with_latest_version(
                                blockParameter.getBlock().getId(),
                                blockParameter.getParameterDict().getId()
                        )
                        .orElseThrow(EntityNotFoundException::new);

        String previousValue = modification.getNewValue();

        ModificationType modificationType = ModificationType.valueOf(modification.getType());
        logger.debug(METHOD_NAME + " Found modification with type " + modificationType);

        if (modification.getVersion().isCommitted()) {
            Modification newModification = new Modification();
            switch (modificationType) {
                case CREATE_PARAMETER, UPDATE_PARAMETER -> {
                    if (modification.getNewValue().equals(previousValue)) {
                        logger.debug(METHOD_NAME + " New value of modification is the same as previous parameter value - modification is not being created");
                        return;
                    }
                    newModification.setBlockParameter(blockParameter);
                    newModification.setNewValue(blockParameter.getValue());
                    newModification.setOldValue(previousValue);
                    newModification.setTimestamp(LocalDateTime.now());
                    newModification.setVersion(
                            versionRepository.find_latest_version_for_block_parameter(
                                            blockParameter.getId(),
                                            false)
                                    .orElseThrow(EntityNotFoundException::new)
                    );
                    newModification.setType(ModificationType.UPDATE_PARAMETER.name());
                }
                case DELETE_PARAMETER ->
                        throw new IllegalStateException("When updating parameter, previous version's modification can't be DELETE_PARAMETER");
            }
            modification = newModification;
        } else {
            switch (modificationType) {
                case CREATE_PARAMETER -> {
                    if (blockParameter.getValue().equals(previousValue)) {
                        logger.debug(METHOD_NAME + " New value of modification is the same as updated parameter value - modification remains the same");
                        return;
                    }
                    logger.debug(METHOD_NAME + " New value of modification is different from updated parameter value - updating new value of modification");
                }
                case UPDATE_PARAMETER -> {
                    if (blockParameter.getValue().equals(previousValue)) {
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

        modificationRepository.save(modification);

        logger.info(METHOD_NAME + " Successfully created proper modification for updating block parameter id: " + blockParameter.getId());
    }

    @Override
    @Transactional
    public void deleteParameterFromBlockModification(BlockParameter blockParameter) {
        final String METHOD_NAME = "[deleteParameterFromBlockModification]";

        Modification modification = new Modification();
        modification.setType(String.valueOf(ModificationType.DELETE_PARAMETER));

        logger.debug(METHOD_NAME + " Searching for version");
        Version version = versionRepository.find_latest_version_for_block_parameter(blockParameter.getId(), false)
                .orElseThrow(EntityNotFoundException::new);

        logger.debug(METHOD_NAME + " Searching parameter modification for version");
        Optional<Modification> modificationOptional =
                modificationRepository.find_modification_for_version_and_parameter_dict(
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
