package com.maciejgogulski.versioncontrolschedule.service.impl;

import com.maciejgogulski.versioncontrolschedule.config.TestConfig;
import com.maciejgogulski.versioncontrolschedule.domain.BlockParameter;
import com.maciejgogulski.versioncontrolschedule.domain.Modification;
import com.maciejgogulski.versioncontrolschedule.enums.ModificationType;
import com.maciejgogulski.versioncontrolschedule.repositories.BlockParameterRepository;
import com.maciejgogulski.versioncontrolschedule.repositories.ModificationRepository;
import com.maciejgogulski.versioncontrolschedule.service.ModificationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@SpringBootTest
public class ModificationServiceImplTest {

    private final Logger logger = LoggerFactory.getLogger(ModificationServiceImplTest.class);

    @Autowired
    private ModificationService modificationService;

    @Autowired
    private BlockParameterRepository blockParameterRepository;

    @Autowired
    private ModificationRepository modificationRepository;

    @Test
    @Transactional
    public void shouldCreateModificationCreateParameter_updateParameterWithinBlockModification_givenDifferentValue() {
        // given
        BlockParameter blockParameter = blockParameterRepository.findById(1L)
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue("102");

        // when
        modificationService.updateParameterWithinBlockModification(blockParameter);

        // then
        Modification modification = modificationRepository
                .find_modification_for_version_and_parameter_dict(1L, 1L, 4L)
                .orElseThrow(EntityNotFoundException::new);

        logger.info(modification.toString());

        Assertions.assertEquals(ModificationType.CREATE_PARAMETER.name(), modification.getType());
        Assertions.assertNull(modification.getOldValue());
        Assertions.assertEquals("102", modification.getNewValue());
    }

    @Test
    @Transactional
    public void shouldCreateModificationCreateParameter_updateParameterWithinBlockModification_givenSameValue() {
        // given
        BlockParameter blockParameter = blockParameterRepository.findById(1L)
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue("101");

        // when
        modificationService.updateParameterWithinBlockModification(blockParameter);

        // then
        Modification modification = modificationRepository
                .find_modification_for_version_and_parameter_dict(1L, 1L, 4L)
                .orElseThrow(EntityNotFoundException::new);

        logger.info(modification.toString());

        Assertions.assertEquals(ModificationType.CREATE_PARAMETER.name(), modification.getType());
        Assertions.assertNull(modification.getOldValue());
        Assertions.assertEquals("101", modification.getNewValue());
    }

    @Test
    @Transactional
    public void shouldCreateModificationUpdateParameter_updateParameterWithinBlockModification_givenDifferentValue() {
        // given
        BlockParameter blockParameter = blockParameterRepository.findById(2L)
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue("Stationary");

        // when
        modificationService.updateParameterWithinBlockModification(blockParameter);

        // then
        Modification modification = modificationRepository
                .find_modification_for_version_and_parameter_dict(3L, 2L, 5L)
                .orElseThrow(EntityNotFoundException::new);

        logger.info(modification.toString());

        Assertions.assertEquals(ModificationType.UPDATE_PARAMETER.name(), modification.getType());
        Assertions.assertEquals("Remote", modification.getOldValue());
        Assertions.assertEquals("Stationary", modification.getNewValue());
    }

    @Test
    @Transactional
    @Disabled // TODO: Find cause of not actually deleting modification, when the deletion in the code is actually running.
    public void shouldDeleteModificationUpdateParameter_updateParameterWithinBlockModification_givenPreviousValue() {
        // given
        BlockParameter blockParameter = blockParameterRepository.findById(3L)
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue("Robert Markson");

        // when
        modificationService.updateParameterWithinBlockModification(blockParameter);

        // then
        Optional<Modification> modification = modificationRepository
                .find_modification_for_version_and_parameter_dict(3L, 2L, 6L);

        logger.info(modification.toString());

        Assertions.assertFalse(modification.isPresent());
    }

    @Test
    @Transactional
    public void shouldNotCreateModification_updateParameterWithinBlockModification_NotModifyingAnyValue() {
        // given
        BlockParameter blockParameter = blockParameterRepository.findById(4L)
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue("Learn students XYZ");

        // when
        modificationService.updateParameterWithinBlockModification(blockParameter);

        // then
        Optional<Modification> modification = modificationRepository
                .find_modification_for_version_and_parameter_dict(3L, 2L, 7L);

        logger.info(modification.toString());

        Assertions.assertFalse(modification.isPresent());
    }

    @Test
    @Transactional
    public void shouldCreateModification_updateParameterWithinBlockModification_more_than_one_version_between_updates() {
        // given
        BlockParameter blockParameter = blockParameterRepository.findById(5L)
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue("102");

        // when
        modificationService.updateParameterWithinBlockModification(blockParameter);

        // then
        Modification modification = modificationRepository
                .find_modification_for_version_and_parameter_dict(6L, 3L, 4L)
                .orElseThrow(EntityNotFoundException::new);

        logger.info(modification.toString());

        Assertions.assertEquals(ModificationType.UPDATE_PARAMETER.name(), modification.getType());
        Assertions.assertEquals("101", modification.getOldValue());
        Assertions.assertEquals("102", modification.getNewValue());
    }
}
