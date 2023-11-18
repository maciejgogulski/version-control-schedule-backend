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

    @Override
    public String toString() {
        String prefix = "W bloku " + blockName;
        return switch (type) {
            case CREATE_PARAMETER ->
                    prefix + " utworzono parametr " + parameterName;
            case UPDATE_PARAMETER ->
                prefix + " zmieniono parametr " + parameterName
                        + " z " + oldValue + " na " + newValue;
            case DELETE_PARAMETER ->
                    prefix + " usunięto parametr " + parameterName;
            default -> "Modification " + id;
        };
    }
}
