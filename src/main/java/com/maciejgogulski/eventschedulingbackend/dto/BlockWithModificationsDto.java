package com.maciejgogulski.eventschedulingbackend.dto;

import java.util.List;

public record BlockWithModificationsDto(
        String name,
        String startDate,
        String endDate,
        List<ModificationDto> modifications
) {
}
