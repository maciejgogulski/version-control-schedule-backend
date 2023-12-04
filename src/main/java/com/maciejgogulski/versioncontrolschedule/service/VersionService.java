package com.maciejgogulski.versioncontrolschedule.service;

import com.maciejgogulski.versioncontrolschedule.dto.ModificationDto;
import com.maciejgogulski.versioncontrolschedule.dto.VersionDto;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VersionService {
    List<ModificationDto> getModificationsForVersion(Long versionId);

    @Transactional
    VersionDto getLatestVersionForSchedule(Long scheduleId);

    @Transactional
    void commitVersion(Long versionId) throws MessagingException;
}
