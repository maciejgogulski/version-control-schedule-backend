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

    // -------------------------------------------------------------------------------
    // ASSIGN PARAM TO BLOCK TESTS
    // -------------------------------------------------------------------------------
    @Test
    @Transactional
    public void givenNewBlockParameter_whenAssignParamToBlock_thenCreateModCreateParam() {
        // given
        Long versionId = 1L;
        Long blockId = 1L;
        Long parameterDictId = 4L;

        BlockParameter blockParameter = blockParameterRepository
                .findById(blockId)
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue("102");

        // when
        modificationService.assignParameterToBlockModification(
                blockParameter
        );
        entityManager.flush();

        // then
        Modification modification = modificationRepository
                .find_modification_for_version_and_parameter_dict(
                        versionId, blockId, parameterDictId
                )
                .orElseThrow(EntityNotFoundException::new);

        Assertions.assertEquals(
                ModificationType.CREATE_PARAMETER.name(),
                modification.getType()
        );
        Assertions.assertNull(modification.getOldValue());
        Assertions.assertEquals("102", modification.getNewValue());
    }

    @Test
    @Transactional
    public void givenPreviousModCreateParam_whenAssignParamToBlock_thenThrowIllegalStateException() {
        // given
        Long blockId = 2L;

        BlockParameter blockParameter = blockParameterRepository.findById(blockId)
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue("Mark Robertson");

        // then
        IllegalStateException thrownIllegalState = Assertions.assertThrows(IllegalStateException.class, () -> {
            // when
            modificationService.assignParameterToBlockModification(blockParameter);
            entityManager.flush();
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
        entityManager.flush();

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
        modificationService.assignParameterToBlockModification(blockParameter);
        entityManager.flush();

        // then
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            modificationRepository.find_modification_for_version_and_parameter_dict(versionId, blockId, parameterDictId)
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

    // -------------------------------------------------------------------------------
    // UPDATE PARAM WITHIN BLOCK TESTS
    // -------------------------------------------------------------------------------

    @Test
    @Transactional
    public void givenNoPreviousMod_whenUpdateParamWithinBlock_thenThrowEntityNotFoundException() {
        // given
        Long blockId = 6L;

        BlockParameter blockParameter = blockParameterRepository.findById(blockId)
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue("Nevada");

        // then
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            // when
            modificationService.updateParameterWithinBlockModification(blockParameter);
            entityManager.flush();
        });
    }

    @Test
    @Transactional
    public void givenPreviousModDeleteParam_whenUpdateParamWithinBlock_thenThrowIllegalStateException() {
        // given
        Long blockId = 7L;

        BlockParameter blockParameter = blockParameterRepository.findById(blockId)
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue("Poland");

        // then
        IllegalStateException thrownIllegalState = Assertions.assertThrows(IllegalStateException.class, () -> {
            // when
            modificationService.updateParameterWithinBlockModification(blockParameter);
            entityManager.flush();
        });

        Assertions.assertEquals(
                "When updating parameter, previous version's modification can't be DELETE_PARAMETER",
                thrownIllegalState.getMessage()
        );
    }

    @Test
    @Transactional
    public void givenPreviousModCreateParamInPreviousVersionDifferentValue_whenUpdateParamWithinBlock_thenCreateModUpdateParam() {
        // given
        Long versionId = 10L;
        Long blockId = 8L;
        Long parameterDictId = 11L;

        BlockParameter blockParameter = blockParameterRepository.findById(blockId)
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue("Europe");

        // when
        modificationService.updateParameterWithinBlockModification(blockParameter);
        entityManager.flush();

        // then
        Modification modification = modificationRepository
                .find_modification_for_version_and_parameter_dict(versionId, blockId, parameterDictId)
                .orElseThrow(EntityNotFoundException::new);

        Assertions.assertEquals(ModificationType.UPDATE_PARAMETER.name(), modification.getType());
        Assertions.assertEquals("Asia", modification.getOldValue());
        Assertions.assertEquals("Europe", modification.getNewValue());
    }

    @Test
    @Transactional
    public void givenPreviousModCreateParamInPreviousVersionSameValue_whenUpdateParamWithinBlock_thenDontCreateMod() {
        // given
        Long versionId = 12L;
        Long blockId = 9L;
        Long parameterDictId = 12L;

        BlockParameter blockParameter = blockParameterRepository.findById(blockId)
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue("BMW");

        // when
        modificationService.updateParameterWithinBlockModification(blockParameter);
        entityManager.flush();

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            modificationRepository.find_modification_for_version_and_parameter_dict(versionId, blockId, parameterDictId)
                    .orElseThrow(EntityNotFoundException::new);
        });
    }

    @Test
    @Transactional
    public void givenPreviousModCreateParamInCurrentVersionSameValue_whenUpdateParamWithinBlock_thenDeleteMod() {
        // given
        Long versionId = 13L;
        Long blockId = 10L;
        Long parameterDictId = 13L;

        BlockParameter blockParameter = blockParameterRepository.findById(blockId)
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue("Real Madrid");

        // when
        modificationService.updateParameterWithinBlockModification(blockParameter);
        entityManager.flush();

        // then
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            modificationRepository.find_modification_for_version_and_parameter_dict(versionId, blockId, parameterDictId)
                    .orElseThrow(EntityNotFoundException::new);
        });
    }

    @Test
    @Transactional
    public void givenPreviousModUpdateParamInCurrentVersionDifferentValue_whenUpdateParamWithinBlock_thenUpdateModNewValue() {
        // given
        Long versionId = 14L;
        Long blockId = 11L;
        Long parameterDictId = 14L;

        BlockParameter blockParameter = blockParameterRepository.findById(blockId)
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue("KFC");

        // when
        modificationService.updateParameterWithinBlockModification(blockParameter);
        entityManager.flush();

        // then
        Modification modification = modificationRepository
                .find_modification_for_version_and_parameter_dict(versionId, blockId, parameterDictId)
                .orElseThrow(EntityNotFoundException::new);

        Assertions.assertEquals(ModificationType.UPDATE_PARAMETER.name(), modification.getType());
        Assertions.assertEquals("McDonalds", modification.getOldValue());
        Assertions.assertEquals("KFC", modification.getNewValue());
    }

    // -------------------------------------------------------------------------------
    // DELETE PARAM FROM BLOCK TESTS
    // -------------------------------------------------------------------------------

    @Test
    @Transactional
    public void givenNoPreviousMod_whenDeleteParamFromBlock_thenThrowEntityNotFoundException() {
        // given
        Long blockId = 12L;

        BlockParameter blockParameter = blockParameterRepository.findById(blockId)
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue("Jan Kowalski");

        // then
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            // when
            modificationService.deleteParameterFromBlockModification(blockParameter);
            entityManager.flush();
        });
    }

    @Test
    @Transactional
    public void givenPreviousModDeleteParam_whenDeleteParamFromBlock_thenThrowIllegalStateException() {
        // given
        Long blockId = 13L;

        BlockParameter blockParameter = blockParameterRepository.findById(blockId)
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue("Lenovo Thinkpad");

        // then
        IllegalStateException thrownIllegalState = Assertions.assertThrows(IllegalStateException.class, () -> {
            // when
            modificationService.deleteParameterFromBlockModification(blockParameter);
            entityManager.flush();
        });

        Assertions.assertEquals(
                "When deleting parameter, previous version's modification can't be DELETE_PARAMETER",
                thrownIllegalState.getMessage()
        );
    }

    @Test
    @Transactional
    public void givenPreviousModUpdateParamInPreviousVersion_whenDeleteParamFromBlock_thenCreateModDeleteParam() {
        // given
        Long versionId = 18L;
        Long blockId = 14L;
        Long parameterDictId = 17L;

        BlockParameter blockParameter = blockParameterRepository.findById(blockId)
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue("North");

        // when
        modificationService.deleteParameterFromBlockModification(blockParameter);
        entityManager.flush();

        // then
        Modification modification = modificationRepository
                .find_modification_for_version_and_parameter_dict(versionId, blockId, parameterDictId)
                .orElseThrow(EntityNotFoundException::new);

        Assertions.assertEquals(ModificationType.DELETE_PARAMETER.name(), modification.getType());
        Assertions.assertEquals("North", modification.getOldValue());
        Assertions.assertNull(modification.getNewValue());
    }

    @Test
    @Transactional
    public void givenPreviousModeCreateParamInCurrentVersion_whenDeleteParamFromBlock_thenDeleteMod() {
        // given
        Long versionId = 19L;
        Long blockId = 15L;
        Long parameterDictId = 18L;

        BlockParameter blockParameter = blockParameterRepository.findById(blockId)
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue("125");

        // when
        modificationService.deleteParameterFromBlockModification(blockParameter);
        entityManager.flush();

        // then
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            modificationRepository.find_modification_for_version_and_parameter_dict(versionId, blockId, parameterDictId)
                    .orElseThrow(EntityNotFoundException::new);
        });
    }

    @Test
    @Transactional
    public void givenPreviousModeUpdateParamInCurrentVersion_whenDeleteParamFromBlock_thenChangeModTypeToDeleteParam() {
        // given
        Long versionId = 20L;
        Long blockId = 16L;
        Long parameterDictId = 19L;

        BlockParameter blockParameter = blockParameterRepository.findById(blockId)
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue("-1 C");

        // when
        modificationService.deleteParameterFromBlockModification(blockParameter);
        entityManager.flush();

        // then
        Modification modification = modificationRepository
                .find_modification_for_version_and_parameter_dict(versionId, blockId, parameterDictId)
                .orElseThrow(EntityNotFoundException::new);

        Assertions.assertEquals(ModificationType.DELETE_PARAMETER.name(), modification.getType());
        Assertions.assertEquals("-1 C", modification.getOldValue());
        Assertions.assertNull(modification.getNewValue());
    }
}
