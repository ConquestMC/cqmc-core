package com.conquestmc.core.dao;

import com.conquestmc.core.model.Rank;
import com.conquestmc.core.model.Statistic;
import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.config.RegisterFieldMappers;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

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

    @SqlUpdate("UPDATE players SET coins = coins + :coins WHERE uuid = :uuid")
    void addCoins(@Bind("uuid") String playerUUID, @Bind("coins") int coinsEarned);

    @SqlUpdate("INSERT INTO player (uuid, name, comp_rank, join_date) VALUES (:uuid, :name, :rank, :joinDate) " +
            "ON DUPLICATE KEY UPDATE uuid = :uuid, name = :name")
    void insertPlayer(@Bind("uuid") String uuid, @Bind("name")String name, @Bind("rank") String rank, @Bind("joinDate") long joinDate);

    @SqlQuery("SELECT rank FROM players WHERE uuid=:uuid")
    String getRank(@Bind("uuid") String playerUUID);
}
