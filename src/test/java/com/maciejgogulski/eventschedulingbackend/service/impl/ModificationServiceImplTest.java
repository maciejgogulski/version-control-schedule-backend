package com.maciejgogulski.eventschedulingbackend.service.impl;

import com.maciejgogulski.eventschedulingbackend.domain.*;
import com.maciejgogulski.eventschedulingbackend.repositories.ModificationRepository;
import com.maciejgogulski.eventschedulingbackend.repositories.ScheduleTagRepository;
import com.maciejgogulski.eventschedulingbackend.repositories.StagedEventRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ModificationServiceImplTest {

    @Autowired
    private ModificationServiceImpl underTest;

    @Autowired
    private ModificationRepository modificationRepository;

    @Autowired
    private StagedEventRepository stagedEventRepository;

    @Autowired
    private ScheduleTagRepository scheduleTagRepository;

    @BeforeTestMethod(value = "shouldCreateModificationCreateParameter")
    void setUp() {
        
    }

    @Test
    @Transactional
    public void shouldCreateModificationCreateParameter() {
        // Create test data
        BlockParameter blockParameter = new BlockParameter(); // Create and set necessary attributes

        StagedEvent stagedEvent = new StagedEvent(); // Create and set necessary attributes
        stagedEventRepository.save(stagedEvent);

        Modification modification = new Modification(); // Create and set necessary attributes
        modificationRepository.save(modification);

        // Call the service method
        underTest.assignParameterToScheduleBlockModification(blockParameter);

        // Retrieve the modified data from the database
        Modification savedModification = modificationRepository.findById(modification.getId()).orElse(null);
        StagedEvent latestUncommittedStagedEvent = stagedEventRepository.findById(stagedEvent.getId()).orElse(null);

        // Assertions to validate the results
        assertNotNull(savedModification);
        assertNotNull(latestUncommittedStagedEvent);
        // Add more assertions as needed to validate the state of the objects and the database.

        // Clean up (optional)
        modificationRepository.delete(savedModification);
        stagedEventRepository.delete(latestUncommittedStagedEvent);
    }

    @Test
    void updateParameterWithinBlockModification() {
    }

    @Test
    void deleteParameterFromScheduleBlockModification() {
    }
}