package com.maciejgogulski.versioncontrolschedule.repositories;

import com.maciejgogulski.versioncontrolschedule.domain.ParameterDict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParameterDictRepository extends JpaRepository<ParameterDict, Long> {

    Optional<ParameterDict> findByName(String name);
}
