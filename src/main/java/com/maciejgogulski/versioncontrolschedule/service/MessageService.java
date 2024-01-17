package com.maciejgogulski.versioncontrolschedule.service;

import com.maciejgogulski.versioncontrolschedule.domain.Schedule;
import com.maciejgogulski.versioncontrolschedule.dto.ModificationDto;
import com.maciejgogulski.versioncontrolschedule.exceptions.NoAddresseesException;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MessageService {
    void notifyAddresseesAboutModifications(Long versionId) throws MessagingException, NoAddresseesException;

    String constructMessage(Schedule schedule, List<ModificationDto> modifications);
}
