package com.maciejgogulski.versioncontrolschedule.service.impl;

import com.maciejgogulski.versioncontrolschedule.dao.ModificationDao;
import com.maciejgogulski.versioncontrolschedule.domain.BlockParameter;
import com.maciejgogulski.versioncontrolschedule.domain.Modification;
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

        if (modificationOptional.isEmpty()) {
            logger.debug(METHOD_NAME + " No modification found, creating modification of type CREATE_PARAMETER");

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

            modificationRepository.save(newModification);
            logger.info(METHOD_NAME + " Successfully created proper modification for creating block parameter id: " + blockParameter.getId());
            return;
        }

        modification = modificationOptional.get();

        ModificationType modificationType = ModificationType.valueOf(modification.getType());
        logger.debug(METHOD_NAME + " Found modification with type " + modificationType);

        if (!modificationType.equals(ModificationType.DELETE_PARAMETER)) {
            throw new IllegalStateException(
                    "When assigning parameter, previous version's modification can only be DELETE_PARAMETER"
            );
        }

        if (modification.getVersion().isCommitted()) {
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

            modificationRepository.save(newModification);
            logger.info(METHOD_NAME + " Successfully created proper modification for creating block parameter id: " + blockParameter.getId());
            return;
        }

        if (blockParameter.getValue().equals(modification.getOldValue())) {
            logger.debug(METHOD_NAME + " Deleting modification");
            modificationRepository.deleteById(modification.getId());
            return;
        }

        logger.debug(METHOD_NAME + " Setting modification type to UPDATE_PARAMETER");
        modification.setBlockParameter(blockParameter);
        modification.setType(ModificationType.UPDATE_PARAMETER.name());
        modification.setNewValue(blockParameter.getValue());
        modification.setTimestamp(LocalDateTime.now());

        modificationRepository.save(modification);
    }

    @Override
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

        ModificationType modificationType = ModificationType.valueOf(modification.getType());
        logger.debug(METHOD_NAME + " Found modification with type " + modificationType);

        if (modificationType.equals(ModificationType.DELETE_PARAMETER)) {
            throw new IllegalStateException(
                    "When updating parameter, previous version's modification can't be DELETE_PARAMETER"
            );
        }

        if (modification.getVersion().isCommitted()) {
            if (blockParameter.getValue().equals(modification.getNewValue())) {
                logger.debug(METHOD_NAME + " Modification is not being created");
                return;
            }

            logger.debug(METHOD_NAME + " Creating new UPDATE_PARAMETER modification");
            Modification newModification = new Modification();
            newModification.setBlockParameter(blockParameter);
            newModification.setNewValue(blockParameter.getValue());
            newModification.setOldValue(modification.getNewValue());
            newModification.setTimestamp(LocalDateTime.now());
            newModification.setVersion(
                    versionRepository.find_latest_version_for_block_parameter(
                                    blockParameter.getId(),
                                    false)
                            .orElseThrow(EntityNotFoundException::new)
            );
            newModification.setType(ModificationType.UPDATE_PARAMETER.name());

            modificationRepository.save(newModification);
            return;
        }

        if (blockParameter.getValue().equals(modification.getOldValue())) {
            logger.debug(METHOD_NAME + " Deleting modification");
            modificationRepository.delete(modification);
            return;
        }

        logger.debug(METHOD_NAME + " Updating new value of modification");
        modification.setBlockParameter(blockParameter);
        modification.setNewValue(blockParameter.getValue());
        modification.setTimestamp(LocalDateTime.now());
        modificationRepository.save(modification);

        logger.info(METHOD_NAME + " Successfully created proper modification for updating block parameter id: " + blockParameter.getId());
    }

    @Override
    public void deleteParameterFromBlockModification(BlockParameter blockParameter) {
        final String METHOD_NAME = "[deleteParameterFromBlockModification]";

        logger.debug(METHOD_NAME + " Searching modification from latest version");
        Modification modification = modificationDao.find_modification_for_parameter_dict_with_latest_version(
                blockParameter.getBlock().getId(),
                blockParameter.getParameterDict().getId()
        ).orElseThrow(EntityNotFoundException::new);

        ModificationType modificationType = ModificationType.valueOf(modification.getType());
        logger.debug(METHOD_NAME + " Found modification with type " + modificationType);

        if (modificationType.equals(ModificationType.DELETE_PARAMETER)) {
            throw new IllegalStateException(
                    "When deleting parameter, previous version's modification can't be DELETE_PARAMETER"
            );
        }

        if (modification.getVersion().isCommitted()) {
            Modification newModification = new Modification();
            newModification.setBlockParameter(blockParameter);
            newModification.setOldValue(blockParameter.getValue());
            newModification.setNewValue(null);
            newModification.setTimestamp(LocalDateTime.now());
            newModification.setVersion(
                    versionRepository.find_latest_version_for_block_parameter(
                                    blockParameter.getId(),
                                    false)
                            .orElseThrow(EntityNotFoundException::new)
            );
            newModification.setType(ModificationType.DELETE_PARAMETER.name());
            modificationRepository.save(newModification);
            return;
        }
        if (modificationType.equals(ModificationType.CREATE_PARAMETER)) {
            logger.debug(METHOD_NAME + " Deleting modification");
            modificationRepository.deleteById(modification.getId());
            return;
        }

        logger.debug(METHOD_NAME + " Setting modification type to DELETE_PARAMETER");
        modification.setOldValue(blockParameter.getValue());
        modification.setNewValue(null);
        modification.setType(ModificationType.DELETE_PARAMETER.name());
        modification.setTimestamp(LocalDateTime.now());
        modification.setBlockParameter(blockParameter);

        modificationRepository.save(modification);

        logger.info(METHOD_NAME + " Successfully created proper modification for deleting block parameter id: " + blockParameter.getId());
    }
}
