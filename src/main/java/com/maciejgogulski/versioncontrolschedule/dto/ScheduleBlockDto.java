package com.maciejgogulski.versioncontrolschedule.dto;

public record ScheduleBlockDto(
        Long id,
        Long scheduleTagId,
        String name,
        String startDate,
        String endDate
        ) {}
