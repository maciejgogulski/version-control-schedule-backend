package com.maciejgogulski.versioncontrolschedule.service;

import com.maciejgogulski.versioncontrolschedule.domain.BlockParameter;
import org.springframework.stereotype.Service;

@Service
public interface ModificationService {
    void assignParameterToScheduleBlockModification(BlockParameter blockParameter);

    void updateParameterWithinBlockModification(BlockParameter blockParameter);

    void deleteParameterFromScheduleBlockModification(BlockParameter blockParameter);
}
