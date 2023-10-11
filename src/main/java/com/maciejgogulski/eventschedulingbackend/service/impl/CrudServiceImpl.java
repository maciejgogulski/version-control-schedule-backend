package com.maciejgogulski.eventschedulingbackend.service.impl;

import com.maciejgogulski.eventschedulingbackend.service.CrudService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic service class for crud operations.
 *
 * @param <T> Entity used to communicate with database.
 * @param <S> Dto used to communicate with network layer.
 */
@Service
public abstract class CrudServiceImpl<T, S> implements CrudService<T, S> {

    // TODO: Throwing end handling entity not found errors.

    protected JpaRepository<T, Long> repository;

    private final Logger logger = LoggerFactory.getLogger(CrudServiceImpl.class);

    @Override
    public S create(S dto) {
        T entity = convertToEntity(dto);
        return convertToDto(repository.save(entity));
    }

    @Override
    public S get(Long id) {
        T entity = repository.findById(id).orElse(null);
        if (entity != null) {
            return convertToDto(entity);
        }
        return null;
    }

    @Override
    public List<S> getAll() {
        List<T> entities = repository.findAll();
        List<S> dtoList = new ArrayList<>();
        for (T entity : entities) {
           dtoList.add(convertToDto(entity));
        }
        return dtoList;
    }

    @Override
    public S update(Long id, S dto) {
        T entity = repository.findById(id).orElse(null);
        if (entity != null) {
            entity = updateEntityFromDto(entity, dto);
            return convertToDto(repository.save(entity));
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    protected abstract T convertToEntity(S dto);

    protected abstract S convertToDto(T entity);

    protected abstract T updateEntityFromDto(T entity, S dto);
}
