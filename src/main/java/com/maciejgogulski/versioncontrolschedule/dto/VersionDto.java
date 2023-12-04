package com.maciejgogulski.versioncontrolschedule.dto;

import java.time.LocalDateTime;

public record VersionDto(Long id, Long scheduleId, boolean committed, LocalDateTime timestamp) {
}
