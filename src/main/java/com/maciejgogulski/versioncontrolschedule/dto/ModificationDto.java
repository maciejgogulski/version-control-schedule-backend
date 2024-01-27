package com.maciejgogulski.versioncontrolschedule.dto;

import com.maciejgogulski.versioncontrolschedule.enums.ModificationType;

import java.time.LocalDateTime;

public record ModificationDto(
        Long id,
        Long versionId,
        Long blockParameterId,
        Long blockId,
        String blockName,
        LocalDateTime blockStartDate,
        LocalDateTime blockEndDate,
        String parameterName,
        ModificationType type,
        String oldValue,
        String newValue,
        LocalDateTime timestamp
) {
    public String getTranslatedModificationType() {
        return switch (type) {
            case CREATE_PARAMETER -> "Utworzono parametr";
            case UPDATE_PARAMETER -> "Zmodyfikowano parametr";
            case DELETE_PARAMETER -> "Usunięto parametr";
        };
    }

    public String getColorClassName() {
        return switch (type) {
            case CREATE_PARAMETER -> "color-create";
            case UPDATE_PARAMETER -> "color-update";
            case DELETE_PARAMETER -> "color-delete";
        };
    }

    public String getTranslatedParameterName() {
        return switch (parameterName) {
            case "Name" -> "Nazwa";
            case "Start date" -> "Data rozpoczęcia";
            case "End date" -> "Data zakończenia";
            default -> parameterName;
        };
    }
}
