package com.maciejgogulski.eventschedulingbackend.dto;

import java.util.Date;

public record ScheduleBlockDto(
        Long id,
        Long scheduleTagId,
        String name,
        String startDate,
        String endDate
        ) {}
