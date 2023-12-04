package com.maciejgogulski.versioncontrolschedule.dto;

public record ParameterDto(
        Long id,
        Long scheduleBlockId,
        String parameterName,
        String value
) {
}
