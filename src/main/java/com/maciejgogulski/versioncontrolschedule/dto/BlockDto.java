package com.maciejgogulski.versioncontrolschedule.dto;

public record BlockDto(
        Long id,
        Long scheduleId,
        String name,
        String startDate,
        String endDate
        ) {}
