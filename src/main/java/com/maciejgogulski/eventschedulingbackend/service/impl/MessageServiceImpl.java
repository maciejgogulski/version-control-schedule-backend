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
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

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

    private final TemplateEngine templateEngine;

    @Value("${e-mail.template-filename}")
    private String templateFile;

    public MessageServiceImpl(MessageRepository messageRepository, StagedEventRepository stagedEventRepository, AddresseeRepository addresseeRepository, ModificationDao modificationDao, JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.messageRepository = messageRepository;
        this.stagedEventRepository = stagedEventRepository;
        this.addresseeRepository = addresseeRepository;
        this.modificationDao = modificationDao;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    @Transactional
    public void notifyAddresseesAboutModifications(Long stagedEventId) throws MessagingException {
        StagedEvent stagedEvent = stagedEventRepository.findById(stagedEventId)
                .orElseThrow(EntityNotFoundException::new);
        ScheduleTag scheduleTag = stagedEvent.getScheduleTag();
        List<Addressee> addressees = addresseeRepository.get_addressees_for_schedule_tag(scheduleTag.getId());
        List<ModificationDto> modifications = modificationDao.get_modifications_for_staged_event(stagedEventId);

        String subject = "Zmiany w planie " + scheduleTag.getName();

        Context context = new Context();
        context.setVariable("subject", subject);
        context.setVariable("modifications", modifications);
        String htmlContent = templateEngine.process(templateFile, context);

        List<String> emails = new ArrayList<>();
        List<Message> messages = new ArrayList<>();

        for (Addressee addressee : addressees) {
            emails.add(addressee.getEmail());

            Message message = new Message();
            message.setStagedEvent(stagedEvent);
            message.setAddressee(addressee);
            message.setContent(htmlContent);
            message.setSendAt(new Date());

            messages.add(message);
        }

        messageRepository.saveAll(messages);

        MimeMessage mailMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);

        helper.setTo(emails.toArray(new String[0]));
        helper.setSubject(subject);
        mailMessage.setContent(htmlContent, "text/html; charset=utf-8");

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

//    private List<BlockWithModificationsDto> groupModificationsByBlocks(List<ModificationDto> modifications) {
//        List<BlockWithModificationsDto> groupedBlocks = new ArrayList<>();
//
//
//    }
}
