package com.maciejgogulski.eventschedulingbackend.service.impl;

import com.maciejgogulski.eventschedulingbackend.domain.Addressee;
import com.maciejgogulski.eventschedulingbackend.dto.AddresseeDto;
import com.maciejgogulski.eventschedulingbackend.repositories.AddresseeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
}
