package com.maciejgogulski.versioncontrolschedule.dao;

import com.maciejgogulski.versioncontrolschedule.dto.ParameterDto;
import com.maciejgogulski.versioncontrolschedule.mappers.ParameterMapper;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BlockParameterDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public BlockParameterDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ParameterDto> get_parameters_for_block(@Param("p_block_id") Long scheduleId) {
        String sql = "SELECT * FROM get_parameters_for_block(:p_block_id)";

        MapSqlParameterSource namedParameters = new MapSqlParameterSource("p_block_id", scheduleId);

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, namedParameters);
        ParameterMapper parameterMapper = new ParameterMapper();

        List<ParameterDto> parameterDtoList = new ArrayList<>();

        while (rowSet.next()) {
            parameterDtoList.add(
                    parameterMapper.mapRow(rowSet)
            );
        }

        return parameterDtoList;
    }
}
