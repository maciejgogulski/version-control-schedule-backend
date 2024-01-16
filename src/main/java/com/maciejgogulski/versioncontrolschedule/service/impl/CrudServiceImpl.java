package com.maciejgogulski.versioncontrolschedule.service.impl;

import com.maciejgogulski.versioncontrolschedule.service.CrudService;
import jakarta.persistence.EntityNotFoundException;
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
        logger.debug("Creating new entity");
        T entity = convertToEntity(dto);
        return convertToDto(repository.save(entity));
    }

    @Override
    public S get(Long id) {
        T entity = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        return convertToDto(entity);
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
        logger.debug("Updating entity with id: " + id);
        T entity = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        entity = updateEntityFromDto(entity, dto);
        logger.debug("Successfully updated entity with id: " + id);
        return convertToDto(repository.save(entity));
    }

    @Override
    public void delete(Long id) {
        logger.debug("Deleting entity with id: " + id);
        repository.deleteById(id);
        logger.debug("Successfully deleted entity with id: " + id);
    }

    protected abstract T convertToEntity(S dto);

    protected abstract S convertToDto(T entity);

    protected abstract T updateEntityFromDto(T entity, S dto);
}
