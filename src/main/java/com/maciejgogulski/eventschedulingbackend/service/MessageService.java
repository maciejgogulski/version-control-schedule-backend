package com.maciejgogulski.eventschedulingbackend.service;

import com.maciejgogulski.eventschedulingbackend.domain.ScheduleTag;
import com.maciejgogulski.eventschedulingbackend.dto.ModificationDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MessageService {
    void notifyAddresseesAboutModifications(Long stagedEventId);

    String constructMessage(ScheduleTag scheduleTag, List<ModificationDto> modifications);
}
