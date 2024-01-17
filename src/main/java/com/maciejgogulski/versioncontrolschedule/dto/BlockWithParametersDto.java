package com.maciejgogulski.versioncontrolschedule.dto;

import java.util.List;

public record BlockWithParametersDto(
        Long id,
        Long scheduleId,
        String name,
        String startDate,
        String endDate,
        List<ParameterDto> parameters
        ) {}
