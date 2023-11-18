package com.maciejgogulski.eventschedulingbackend.service.impl;

import com.maciejgogulski.eventschedulingbackend.dao.ModificationDao;
import com.maciejgogulski.eventschedulingbackend.domain.Addressee;
import com.maciejgogulski.eventschedulingbackend.domain.Message;
import com.maciejgogulski.eventschedulingbackend.domain.ScheduleTag;
import com.maciejgogulski.eventschedulingbackend.domain.StagedEvent;
import com.maciejgogulski.eventschedulingbackend.dto.ModificationDto;
import com.maciejgogulski.eventschedulingbackend.repositories.AddresseeRepository;
import com.maciejgogulski.eventschedulingbackend.repositories.MessageRepository;
import com.maciejgogulski.eventschedulingbackend.repositories.StagedEventRepository;
import com.maciejgogulski.eventschedulingbackend.service.MessageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    private final StagedEventRepository stagedEventRepository;

    private final AddresseeRepository addresseeRepository;

    private final ModificationDao modificationDao;

    private final JavaMailSender mailSender;

    public MessageServiceImpl(MessageRepository messageRepository, StagedEventRepository stagedEventRepository, AddresseeRepository addresseeRepository, ModificationDao modificationDao, JavaMailSender mailSender) {
        this.messageRepository = messageRepository;
        this.stagedEventRepository = stagedEventRepository;
        this.addresseeRepository = addresseeRepository;
        this.modificationDao = modificationDao;
        this.mailSender = mailSender;
    }

    @Override
    @Transactional
    public void notifyAddresseesAboutModifications(Long stagedEventId) {
        StagedEvent stagedEvent = stagedEventRepository.findById(stagedEventId)
                .orElseThrow(EntityNotFoundException::new);
        ScheduleTag scheduleTag = stagedEvent.getScheduleTag();
        List<Addressee> addressees = addresseeRepository.get_addressees_for_schedule_tag(scheduleTag.getId());
        List<ModificationDto> modifications = modificationDao.get_modifications_for_staged_event(stagedEventId);
        String content = constructMessage(scheduleTag, modifications);

        List<String> emails = new ArrayList<>();
        List<Message> messages = new ArrayList<>();

        for (Addressee addressee : addressees) {
            emails.add(addressee.getEmail());

            Message message = new Message();
            message.setStagedEvent(stagedEvent);
            message.setAddressee(addressee);
            message.setContent(content);
            message.setSendAt(new Date());

            messages.add(message);
        }

        messageRepository.saveAll(messages);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(emails.toArray(new String[0]));
        mailMessage.setSubject("Zmiany w planie " + scheduleTag.getName());
        mailMessage.setText(content);

        mailSender.send(mailMessage);
    }

    @Override
    public String constructMessage(ScheduleTag scheduleTag, List<ModificationDto> modifications) {
        StringBuilder builder = new StringBuilder("""
                Szanowni państwo,
                                
                informujemy, że plan %s uległ zmianie.
                Oto lista zmian:
                               
                """.formatted(scheduleTag.getName()));
        builder.append("\n");
        for (ModificationDto modification : modifications) {
            builder.append(modification);
            builder.append("\n");
        }
        builder.append("Prosimy o zapoznanie się z powyższymi zmianami.\n");
        return builder.toString();
    }
}
