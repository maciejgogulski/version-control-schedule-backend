package com.maciejgogulski.versioncontrolschedule.dao;

import com.maciejgogulski.versioncontrolschedule.domain.Modification;
import com.maciejgogulski.versioncontrolschedule.dto.ModificationDto;
import com.maciejgogulski.versioncontrolschedule.mappers.ModificationDtoMapper;
import com.maciejgogulski.versioncontrolschedule.mappers.ModificationMapper;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        ModificationDtoMapper modificationDtoMapper = new ModificationDtoMapper();

        List<ModificationDto> modificationDtoList = new ArrayList<>();

        while (rowSet.next()) {
            modificationDtoList.add(
                    modificationDtoMapper.mapRow(rowSet)
            );
        }

        return modificationDtoList;
    }

    public Optional<Modification> find_modification_for_parameter_dict_with_latest_version(@Param("p_block_id") Long blockId, @Param("p_parameter_dict_id") Long parameterDictId) {
        String sql = "SELECT * FROM find_modification_for_parameter_dict_with_latest_version(:p_block_id, :p_parameter_dict_id)";

        MapSqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("p_block_id", blockId)
                .addValue("p_parameter_dict_id", parameterDictId);

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, namedParameters);
        ModificationMapper modificationMapper = new ModificationMapper();

        Modification modification = rowSet.next()
                ? modificationMapper.mapRow(rowSet)
                : null;

        return Optional.ofNullable(modification);
    }
}
