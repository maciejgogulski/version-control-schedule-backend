package com.maciejgogulski.eventschedulingbackend.dao;

import com.maciejgogulski.eventschedulingbackend.domain.Modification;
import com.maciejgogulski.eventschedulingbackend.dto.ModificationDto;
import com.maciejgogulski.eventschedulingbackend.dto.ParameterDto;
import com.maciejgogulski.eventschedulingbackend.mappers.ModificationMapper;
import com.maciejgogulski.eventschedulingbackend.mappers.ParameterMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ModificationDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ModificationDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ModificationDto> get_modifications_for_staged_event(@Param("p_staged_event_id") Long stagedEventId) {
        String sql = "SELECT * FROM get_modifications_for_staged_event(:p_staged_event_id)";

        MapSqlParameterSource namedParameters = new MapSqlParameterSource("p_staged_event_id", stagedEventId);

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, namedParameters);
        ModificationMapper modificationMapper = new ModificationMapper();

        List<ModificationDto> modificationDtoList = new ArrayList<>();

        while (rowSet.next()) {
            modificationDtoList.add(
                    modificationMapper.mapRow(rowSet)
            );
        }

        return modificationDtoList;
    }
}
