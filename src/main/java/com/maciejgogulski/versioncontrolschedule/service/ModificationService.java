package com.maciejgogulski.versioncontrolschedule.service;

import com.maciejgogulski.versioncontrolschedule.domain.BlockParameter;
import org.springframework.stereotype.Service;

@Service
public interface ModificationService {
    void assignParameterToBlockModification(BlockParameter blockParameter);

    void updateParameterWithinBlockModification(BlockParameter blockParameter);

    void deleteParameterFromBlockModification(BlockParameter blockParameter);
}
