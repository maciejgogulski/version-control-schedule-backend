package com.maciejgogulski.versioncontrolschedule.dto;

import java.time.LocalDateTime;

public record StagedEventDto(Long id, Long scheduleTagId, boolean committed, LocalDateTime timestamp) {
}
