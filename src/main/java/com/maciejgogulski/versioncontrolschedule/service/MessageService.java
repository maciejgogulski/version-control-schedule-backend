package com.maciejgogulski.versioncontrolschedule.service;

import com.maciejgogulski.versioncontrolschedule.domain.Schedule;
import com.maciejgogulski.versioncontrolschedule.dto.ModificationDto;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MessageService {
    void notifyAddresseesAboutModifications(Long stagedEventId) throws MessagingException;

    String constructMessage(Schedule schedule, List<ModificationDto> modifications);
}
