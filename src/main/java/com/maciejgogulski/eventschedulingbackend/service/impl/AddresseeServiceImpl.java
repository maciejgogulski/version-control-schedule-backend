package com.maciejgogulski.eventschedulingbackend.service.impl;

import com.maciejgogulski.eventschedulingbackend.domain.Addressee;
import com.maciejgogulski.eventschedulingbackend.domain.ScheduleTag;
import com.maciejgogulski.eventschedulingbackend.dto.AddresseeDto;
import com.maciejgogulski.eventschedulingbackend.repositories.AddresseeRepository;
import com.maciejgogulski.eventschedulingbackend.repositories.ScheduleTagRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.rmi.AlreadyBoundException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;$

@Service
public class AddresseeServiceImpl extends CrudServiceImpl<Addressee, AddresseeDto> {

    private final Logger logger = LoggerFactory.getLogger(AddresseeServiceImpl.class);

    private final ScheduleTagRepository scheduleTagRepository;

    public AddresseeServiceImpl(AddresseeRepository repository, ScheduleTagRepository scheduleTagRepository) {
        this.scheduleTagRepository = scheduleTagRepository;
        this.repository = repository;
    }

    @Override
    protected Addressee convertToEntity(AddresseeDto dto) {
        Addressee addressee = new Addressee();

        addressee.setId(dto.id());
        addressee.setEmail(dto.email());
        addressee.setFirstName(dto.firstName());
        addressee.setLastName(dto.lastName());

        return addressee;
    }

    @Override
    protected AddresseeDto convertToDto(Addressee entity) {
        return new AddresseeDto(
                entity.getId(),
                entity.getEmail(),
                entity.getFirstName(),
                entity.getLastName()
        );
    }

    @Override
    protected Addressee updateEntityFromDto(Addressee addressee, AddresseeDto dto) {
        addressee.setId(dto.id());
        addressee.setEmail(dto.email());
        addressee.setFirstName(dto.firstName());
        addressee.setLastName(dto.lastName());

        return addressee;
    }

    @Transactional
    public List<AddresseeDto> getAddressesByScheduleTagId(Long scheduleTagId) {
        final String METHOD_TAG = "[getAddressesByScheduleTagId] ";
        logger.debug(METHOD_TAG + "Getting addressees for schedule tag id: " + scheduleTagId);
        List<Addressee> addresseeList = ((AddresseeRepository) repository).get_addressees_for_schedule_tag(scheduleTagId);
        List<AddresseeDto> dtoList = new LinkedList<>();
        for (Addressee addressee : addresseeList) {
            dtoList.add(
                    convertToDto(addressee)
            );
        }
        logger.debug(METHOD_TAG + "Fetched " + addresseeList.size() + " addressees for schedule tag id: " + scheduleTagId);
        return dtoList;
    }

    @Transactional
    public void assignAddresseeToScheduleTagId(Long addresseeId, Long scheduleTagId) throws AlreadyBoundException {
        final String METHOD_TAG = "[assignAddresseeToScheduleTagId] ";
        logger.debug(METHOD_TAG + "Assigning addressee id: " + addresseeId + " to schedule tag id: " + scheduleTagId);
        Addressee addressee = repository.findById(addresseeId)
                .orElseThrow(EntityNotFoundException::new);
        ScheduleTag scheduleTag = scheduleTagRepository.findById(scheduleTagId)
                .orElseThrow(EntityNotFoundException::new);

        List<Addressee> addressees = ((AddresseeRepository) repository).get_addressees_for_schedule_tag(scheduleTagId);
        if (addressees.contains(addressee)) {
            logger.error(METHOD_TAG + "Addressee id: " + addresseeId + " already assigned to schedule tag id: " + scheduleTagId);
            throw new AlreadyBoundException(METHOD_TAG + "Addressee id: " + addresseeId + " already assigned to schedule tag id: " + scheduleTagId);
        }

        addressees.add(addressee);
        scheduleTag.setAddressees(new HashSet<>(addressees));

        scheduleTagRepository.save(scheduleTag);
        logger.debug(METHOD_TAG + "Successfully assigned addressee id: " + addresseeId + " to schedule tag id: " + scheduleTagId);
    }
}
