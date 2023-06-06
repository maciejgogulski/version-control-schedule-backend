package com.maciejgogulski.eventschedulingbackend.dto;

import java.util.Date;

public record BlocksForScheduleByDayRequestDto(Long scheduleTagId, Date day) {}
