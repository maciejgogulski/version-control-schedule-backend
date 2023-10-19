package com.maciejgogulski.eventschedulingbackend.dto;

import java.time.LocalDateTime;

public record StagedEventDto(Long id, Long scheduleTagId, boolean committed, LocalDateTime timestamp) {
}
