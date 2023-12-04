package com.maciejgogulski.versioncontrolschedule.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CrudService<T, S> {
    S create(S dto);

    S get(Long id);

    List<S> getAll();

    S update(Long id, S dto);

    void delete(Long id);
}
