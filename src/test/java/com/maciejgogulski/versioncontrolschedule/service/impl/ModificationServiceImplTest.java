package com.maciejgogulski.versioncontrolschedule.service.impl;

import com.maciejgogulski.versioncontrolschedule.config.TestConfig;
import com.maciejgogulski.versioncontrolschedule.domain.BlockParameter;
import com.maciejgogulski.versioncontrolschedule.domain.Modification;
import com.maciejgogulski.versioncontrolschedule.enums.ModificationType;
import com.maciejgogulski.versioncontrolschedule.repositories.BlockParameterRepository;
import com.maciejgogulski.versioncontrolschedule.repositories.ModificationRepository;
import com.maciejgogulski.versioncontrolschedule.service.ModificationService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@SpringBootTest
public class ModificationServiceImplTest {

    @Autowired
    private ModificationService modificationService;

    @Autowired
    private BlockParameterRepository blockParameterRepository;

    @Autowired
    private ModificationRepository modificationRepository;

    @Autowired
    private EntityManager entityManager;

    @Transactional
    private Optional<Modification> surroundFindingModificationWithTransaction(Long versionId, Long blockId, Long parameterDictId) {
        return modificationRepository
                .find_modification_for_version_and_parameter_dict(versionId, blockId, parameterDictId);
    }

    @Transactional
    private void surroundAssignParamToBlockWithTransaction(BlockParameter blockParameter) {
        modificationService.assignParameterToBlockModification(blockParameter);
    }

    @Test
    @Transactional
    public void givenNewBlockParameter_whenAssignParamToBlock_thenCreateModCreateParam() {
        // given
        Long versionId = 1L;
        Long blockId = 1L;
        Long parameterDictId = 4L;

        BlockParameter blockParameter = blockParameterRepository.findById(blockId)
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue("102");

        // when
        modificationService.assignParameterToBlockModification(blockParameter);

        // then
        Modification modification = modificationRepository
                .find_modification_for_version_and_parameter_dict(versionId, blockId, parameterDictId)
                .orElseThrow(EntityNotFoundException::new);

        Assertions.assertEquals(ModificationType.CREATE_PARAMETER.name(), modification.getType());
        Assertions.assertNull(modification.getOldValue());
        Assertions.assertEquals("102", modification.getNewValue());
    }

    @Test
    @Transactional
    public void givenPreviousModCreateParam_whenAssignParamToBlock_thenThrowIllegalStateException() {
        // given
        Long versionId = 2L;
        Long blockId = 2L;
        Long parameterDictId = 5L;

        BlockParameter blockParameter = blockParameterRepository.findById(blockId)
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue("Mark Robertson");

        // then
        IllegalStateException thrownIllegalState = Assertions.assertThrows(IllegalStateException.class, () -> {
            // when
            modificationService.assignParameterToBlockModification(blockParameter);
        });

        Assertions.assertEquals(
                "When assigning parameter, previous version's modification can only be DELETE_PARAMETER",
                thrownIllegalState.getMessage()
        );
    }

    @Test
    @Transactional
    public void givenPreviousModDeleteParamInPreviousVersion_whenAssignParamToBlock_thenCreateModCreateParam() {
        // given
        Long versionId = 4L;
        Long blockId = 3L;
        Long parameterDictId = 6L;

        BlockParameter blockParameter = blockParameterRepository.findById(blockId)
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue("Remote");

        // when
        modificationService.assignParameterToBlockModification(blockParameter);

        // then
        Modification modification = modificationRepository
                .find_modification_for_version_and_parameter_dict(versionId, blockId, parameterDictId)
                .orElseThrow(EntityNotFoundException::new);

        Assertions.assertEquals(ModificationType.CREATE_PARAMETER.name(), modification.getType());
        Assertions.assertNull(modification.getOldValue());
        Assertions.assertEquals("Remote", modification.getNewValue());
    }

    @Test
    @Transactional
    public void givenPreviousModDeleteParamInCurrentVersionSameValue_whenAssignParamToBlock_thenDeleteMod() {
        // given
        Long versionId = 5L;
        Long blockId = 4L;
        Long parameterDictId = 7L;

        BlockParameter blockParameter = blockParameterRepository.findById(blockId)
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue("Polytechnic");

        // when
        surroundAssignParamToBlockWithTransaction(blockParameter);
        entityManager.flush();

        // then
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            surroundFindingModificationWithTransaction(versionId, blockId, parameterDictId)
                    .orElseThrow(EntityNotFoundException::new);
        });
    }

    @Test
    @Transactional
    public void givenPreviousModDeleteParamInCurrentVersionDifferentValue_whenAssignParamToBlock_thenCreateModUpdateParam() {
        // given
        Long versionId = 6L;
        Long blockId = 5L;
        Long parameterDictId = 8L;

        BlockParameter blockParameter = blockParameterRepository.findById(blockId)
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue("2");

        // when
        modificationService.assignParameterToBlockModification(blockParameter);
        entityManager.flush();

        // then
        Modification modification = modificationRepository
                .find_modification_for_version_and_parameter_dict(versionId, blockId, parameterDictId)
                .orElseThrow(EntityNotFoundException::new);

        Assertions.assertEquals(ModificationType.UPDATE_PARAMETER.name(), modification.getType());
        Assertions.assertEquals("1", modification.getOldValue());
        Assertions.assertEquals("2", modification.getNewValue());
    }
}
