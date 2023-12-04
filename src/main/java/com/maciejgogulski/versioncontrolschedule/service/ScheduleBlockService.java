package com.maciejgogulski.versioncontrolschedule.service;

import com.maciejgogulski.versioncontrolschedule.domain.Block;
import com.maciejgogulski.versioncontrolschedule.dto.ParameterDto;
import com.maciejgogulski.versioncontrolschedule.dto.ScheduleBlockDto;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

@Service
public interface ScheduleBlockService {

    ScheduleBlockDto addBlock(ScheduleBlockDto scheduleBlockDto) throws ParseException;

    void assignRequiredParameters(Block block);

    ScheduleBlockDto getBlock(Long scheduleBlockId);

    ScheduleBlockDto updateBlock(ScheduleBlockDto scheduleBlockDto) throws ParseException;

    void deleteBlock(Long scheduleBlockId);

    List<ScheduleBlockDto> getBlocksForScheduleByDay(Long scheduleTagId, String day);

    void assignParameterToBlock(ParameterDto parameterDto);

    List<ParameterDto> getParametersForBlock(Long scheduleBlockId);

    void updateParameterWithinBlock(ParameterDto parameterDto);

    void deleteParameterFromBlock(Long blockParameterId);
}
