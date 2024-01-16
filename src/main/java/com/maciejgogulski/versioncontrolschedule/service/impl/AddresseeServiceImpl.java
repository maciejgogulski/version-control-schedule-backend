package com.maciejgogulski.versioncontrolschedule.service.impl;

import com.maciejgogulski.versioncontrolschedule.domain.Addressee;
import com.maciejgogulski.versioncontrolschedule.dto.AddresseeDto;
import com.maciejgogulski.versioncontrolschedule.repositories.AddresseeRepository;
import com.maciejgogulski.versioncontrolschedule.service.AddresseeService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.rmi.AlreadyBoundException;
import java.util.LinkedList;
import java.util.List;

@Service
public class AddresseeServiceImpl extends CrudServiceImpl<Addressee, AddresseeDto> implements AddresseeService {

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
    @Override
    public List<AddresseeDto> getAddressesByScheduleId(Long scheduleId) {
        List<Addressee> addresseeList = ((AddresseeRepository) repository).get_addressees_for_schedule(scheduleId);
        List<AddresseeDto> dtoList = new LinkedList<>();
        for (Addressee addressee : addresseeList) {
            dtoList.add(
                    convertToDto(addressee)
            );
        }
        return dtoList;
    }

    @Override
    @Transactional
    public void assignAddresseeToSchedule(Long addresseeId, Long scheduleId) throws AlreadyBoundException {
        final String METHOD_TAG = "[assignAddresseeToScheduleTagId] ";
        logger.debug(METHOD_TAG + "Assigning addressee id: " + addresseeId + " to schedule id: " + scheduleId);

        ((AddresseeRepository) repository).assign_addressee_to_schedule(addresseeId, scheduleId);

        logger.debug(METHOD_TAG + "Successfully assigned addressee id: " + addresseeId + " to schedule id: " + scheduleId);
    }
}
