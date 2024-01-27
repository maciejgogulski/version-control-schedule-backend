package com.maciejgogulski.versioncontrolschedule.dto;

import com.maciejgogulski.versioncontrolschedule.enums.BlockModificationType;

import java.util.List;

public record BlockWithModificationsDto(
        Long id,
        String name,
        String startDate,
        String endDate,
        BlockModificationType blockModificationType,
        List<ModificationDto> modifications
) {
    public String getColorClassName() {
        return switch (blockModificationType) {
            case CREATE_BLOCK -> "color-create";
            case UPDATE_BLOCK -> "color-update";
            case DELETE_BLOCK -> "color-delete";
        };
    }

    public String getTranslatedBlockModificationType () {
        return switch (blockModificationType) {
            case CREATE_BLOCK -> "Utworzono blok";
            case UPDATE_BLOCK -> "Zmodyfikowano blok";
            case DELETE_BLOCK -> "Usunięto blok";
        };
    }
}
