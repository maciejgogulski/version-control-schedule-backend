package com.maciejgogulski.versioncontrolschedule.mappers;

import com.maciejgogulski.versioncontrolschedule.dto.ModificationDto;
import com.maciejgogulski.versioncontrolschedule.enums.ModificationType;
import org.springframework.jdbc.support.rowset.SqlRowSet;


public class ModificationDtoMapper {

    public ModificationDto mapRow(SqlRowSet rowSet) {
        return new ModificationDto(
                rowSet.getLong("id"),
                rowSet.getLong("version_id"),
                rowSet.getLong("block_parameter_id"),
                rowSet.getLong("block_id"),
                rowSet.getString("block_name"),
                (rowSet.getTimestamp("block_start_date") != null)
                        ? rowSet.getTimestamp("block_start_date").toLocalDateTime()
                        : null,
                (rowSet.getTimestamp("block_end_date") != null)
                        ? rowSet.getTimestamp("block_end_date").toLocalDateTime()
                        : null,
                rowSet.getString("parameter_name"),
                ModificationType.valueOf(rowSet.getString("type")),
                rowSet.getString("old_value"),
                rowSet.getString("new_value"),
                (rowSet.getTimestamp("timestamp") != null)
                        ? rowSet.getTimestamp("timestamp").toLocalDateTime()
                        : null
        );
    }
}
