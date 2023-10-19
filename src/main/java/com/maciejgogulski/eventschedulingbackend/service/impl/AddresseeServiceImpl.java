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
import java.util.List;

@Service
public class AddresseeServiceImpl extends CrudServiceImpl<Addressee, AddresseeDto> {

    private final Logger logger = LoggerFactory.getLogger(AddresseeServiceImpl.class);

    public AddresseeServiceImpl(AddresseeRepository repository) {
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

        ((AddresseeRepository) repository).assign_addressee_to_schedule_tag(addresseeId, scheduleTagId);

        logger.debug(METHOD_TAG + "Successfully assigned addressee id: " + addresseeId + " to schedule tag id: " + scheduleTagId);
    }
}
