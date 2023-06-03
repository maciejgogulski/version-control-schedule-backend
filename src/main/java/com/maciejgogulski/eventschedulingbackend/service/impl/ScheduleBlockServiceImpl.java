package com.maciejgogulski.eventschedulingbackend.service.impl;

import com.maciejgogulski.eventschedulingbackend.domain.ScheduleBlock;
import com.maciejgogulski.eventschedulingbackend.repositories.ScheduleBlockRepository;
import com.maciejgogulski.eventschedulingbackend.service.ScheduleBlockService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ScheduleBlockServiceImpl implements ScheduleBlockService {

    @Autowired
    private ScheduleBlockRepository scheduleBlockRepository;

    @Override
    public ScheduleBlock addScheduleBlock(ScheduleBlock scheduleBlock) {
        return scheduleBlockRepository.save(scheduleBlock);
    }

    @Override
    public ScheduleBlock getScheduleBlock(Long scheduleBlockId) {
        Optional<ScheduleBlock> scheduleBlock = scheduleBlockRepository.findById(scheduleBlockId);

        if (scheduleBlock.isPresent()) {
            return scheduleBlock.get();
        } else {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public ScheduleBlock updateScheduleBlock(ScheduleBlock scheduleBlock) {
        if (scheduleBlockRepository.findById(scheduleBlock.getId()).isPresent()) {
            return scheduleBlockRepository.save(scheduleBlock);
        } else {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public void deleteScheduleBlock(Long scheduleBlockId) {
        Optional<ScheduleBlock> scheduleBlock = scheduleBlockRepository.findById(scheduleBlockId);

        if (scheduleBlock.isPresent()) {
            scheduleBlockRepository.delete(scheduleBlock.get());
        } else {
            throw new EntityNotFoundException();
        }
    }
}
