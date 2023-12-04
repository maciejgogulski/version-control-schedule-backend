package com.maciejgogulski.versioncontrolschedule.dto;

import com.maciejgogulski.versioncontrolschedule.enums.ModificationType;

import java.time.LocalDateTime;

public record ModificationDto(
        Long id,
        Long versionId,
        Long blockParameterId,
        String blockName,
        String parameterName,
        ModificationType type,
        String oldValue,
        String newValue,
        LocalDateTime timestamp
) {

    @Override
    public String toString() {
        return switch (type) {
            case CREATE_PARAMETER ->
                    "utworzono parametr " + parameterName;
            case UPDATE_PARAMETER ->
                "zmieniono parametr " + parameterName
                        + " z " + oldValue + " na " + newValue;
            case DELETE_PARAMETER ->
                    "usunięto parametr " + parameterName;
            default -> "Modification " + id;
        };
    }
}
