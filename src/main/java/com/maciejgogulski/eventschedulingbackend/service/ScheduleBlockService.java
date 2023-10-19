package com.maciejgogulski.eventschedulingbackend.service;

import com.maciejgogulski.eventschedulingbackend.dto.ScheduleBlockDto;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

@Service
public interface ScheduleBlockService {

    ScheduleBlockDto addScheduleBlock(ScheduleBlockDto scheduleBlockDto) throws ParseException;

    ScheduleBlockDto getScheduleBlock(Long scheduleBlockId);

    ScheduleBlockDto updateScheduleBlock(ScheduleBlockDto scheduleBlockDto) throws ParseException;

    void deleteScheduleBlock(Long scheduleBlockId);

    List<ScheduleBlockDto> getScheduleBlocksForScheduleByDay(Long scheduleTagId, String day);

    void assignParameterToBlock(String parameterName, String parameterValue, Long blockId);
}
