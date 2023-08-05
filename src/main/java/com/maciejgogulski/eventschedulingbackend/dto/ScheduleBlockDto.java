package com.maciejgogulski.eventschedulingbackend.dto;

public record ScheduleBlockDto(
        Long id,
        Long scheduleTagId,
        String name,
        String startDate,
        String endDate
        ) {}
