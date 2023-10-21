package com.maciejgogulski.eventschedulingbackend.dto;

import com.maciejgogulski.eventschedulingbackend.enums.ModificationType;

public record ModificationDto(
        Long id,
        Long stagedEventId,
        Long blockParameterId,
        String parameterName,
        ModificationType type,
        String oldValue,
        String newValue
) {
}
