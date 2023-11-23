package com.maciejgogulski.eventschedulingbackend.service.impl;

import com.maciejgogulski.eventschedulingbackend.config.TestConfig;
import com.maciejgogulski.eventschedulingbackend.domain.BlockParameter;
import com.maciejgogulski.eventschedulingbackend.domain.Modification;
import com.maciejgogulski.eventschedulingbackend.enums.ModificationType;
import com.maciejgogulski.eventschedulingbackend.repositories.BlockParameterRepository;
import com.maciejgogulski.eventschedulingbackend.repositories.ModificationRepository;
import com.maciejgogulski.eventschedulingbackend.service.ModificationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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

    @BeforeAll
    @FlywayTest
    public static void before() {}

    @Test
    @Transactional
    public void shouldCreateModificationCreateParameter_updateParameterWithinBlockModification() {
        // given
        BlockParameter blockParameter = blockParameterRepository.findById(1L)
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue("102");

        // when
        modificationService.updateParameterWithinBlockModification(blockParameter);

        // then
        Modification modification = modificationRepository
                .find_modification_for_staged_event_and_parameter_dict(1L, 1L, 1L)
                .orElseThrow(EntityNotFoundException::new);

        Assertions.assertEquals(ModificationType.CREATE_PARAMETER.name(), modification.getType());
        Assertions.assertNull(modification.getOldValue());
        Assertions.assertEquals("102", modification.getNewValue());
    }
}
