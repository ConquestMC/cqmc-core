package com.conquestmc.core.dao;

import com.conquestmc.core.model.Statistic;
import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.config.RegisterFieldMappers;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.UUID;
@RegisterFieldMappers({
        @RegisterFieldMapper(Statistic.class)
})
public interface PlayerDao {

    @SqlQuery("CREATE TABLE IF NOT EXISTS `players` (uuid VARCHAR(63), name VARCHAR(16), comp_rank VARCHAR(255), coins BIGINT NOT NULL DEFAULT 0, join_date BIGINT NOT NULL, PRIMARY KEY(uuid));")
    void createPlayerTable();

    @SqlQuery("CREATE TABLE IF NOT EXISTS `statistics` (player_uuid VARCHAR(63), name VARCHAR(16), value INT NOT NULL DEFAULT 0, PRIMARY KEY (uuid, name), FOREIGN KEY (player_uuid) REFERENCES player(uuid));")
    void createStatisticsTable();

    default void createTables() {
        createPlayerTable();
        createStatisticsTable();
    }

    @SqlQuery("SELECT coins FROM players WHERE uuid=:uuid")
    int getCoins(@Bind("uuid") String playerUUID);
}
