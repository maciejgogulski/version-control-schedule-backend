package com.maciejgogulski.eventschedulingbackend.service;

import com.maciejgogulski.eventschedulingbackend.dto.ModificationDto;
import com.maciejgogulski.eventschedulingbackend.dto.StagedEventDto;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StagedEventService {
    List<ModificationDto> getModificationsForStagedEvent(Long stagedEventId);

    @Transactional
    StagedEventDto getLatestStagedEventForSchedule(Long scheduleTagId);

    @Transactional
    void commitStagedEvent(Long stagedEventId) throws MessagingException;
}
