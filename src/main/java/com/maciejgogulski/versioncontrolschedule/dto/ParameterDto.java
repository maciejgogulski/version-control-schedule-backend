package com.maciejgogulski.versioncontrolschedule.dto;

public record ParameterDto(
        Long id,
        Long blockId,
        String parameterName,
        String value
) {
}
