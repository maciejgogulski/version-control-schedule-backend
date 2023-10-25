package com.maciejgogulski.eventschedulingbackend.service;

import com.maciejgogulski.eventschedulingbackend.domain.BlockParameter;
import com.maciejgogulski.eventschedulingbackend.domain.ScheduleBlock;
import org.springframework.stereotype.Service;

@Service
public interface ModificationService {
    void assignParameterToScheduleBlockModification(BlockParameter blockParameter);
}
