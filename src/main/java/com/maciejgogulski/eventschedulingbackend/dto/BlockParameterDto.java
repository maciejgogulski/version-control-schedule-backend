package com.maciejgogulski.eventschedulingbackend.dto;

public record BlockParameterDto(
        Long id,
        Long scheduleBlockId,
        String parameterName,
        String value
) {
}
