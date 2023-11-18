package com.maciejgogulski.eventschedulingbackend.dto;

import com.maciejgogulski.eventschedulingbackend.enums.ModificationType;

import java.time.LocalDateTime;

public record ModificationDto(
        Long id,
        Long stagedEventId,
        Long blockParameterId,
        String blockName,
        String parameterName,
        ModificationType type,
        String oldValue,
        String newValue,
        LocalDateTime timestamp
) {
}
