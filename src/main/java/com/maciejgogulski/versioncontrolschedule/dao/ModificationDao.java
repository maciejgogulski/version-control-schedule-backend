package com.maciejgogulski.versioncontrolschedule.dao;

import com.maciejgogulski.versioncontrolschedule.dto.ModificationDto;
import com.maciejgogulski.versioncontrolschedule.mappers.ModificationMapper;
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

    public List<ModificationDto> get_modifications_for_version(@Param("p_version_id") Long versionId) {
        String sql = "SELECT * FROM get_modifications_for_version(:p_version_id)";

        MapSqlParameterSource namedParameters = new MapSqlParameterSource("p_version_id", versionId);

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
