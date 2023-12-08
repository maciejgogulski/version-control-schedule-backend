package com.maciejgogulski.versioncontrolschedule.repositories;

import com.maciejgogulski.versioncontrolschedule.domain.Modification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModificationRepository extends JpaRepository<Modification, Long> {

    @Procedure
    Optional<Modification> find_modification_for_parameter_dict_from_latest_version(Long blockId, Long parameterDictId);
}
