package com.maciejgogulski.versioncontrolschedule.dto;

import java.util.List;

public record BlockWithModificationsDto(
        Long id,
        String name,
        String startDate,
        String endDate,
        List<ModificationDto> modifications
) {
}
