package com.maciejgogulski.eventschedulingbackend.dto;

public record ParameterDto(
        Long id,
        Long scheduleBlockId,
        String parameterName,
        String value
) {
}