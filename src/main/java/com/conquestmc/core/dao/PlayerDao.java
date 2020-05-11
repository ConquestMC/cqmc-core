package com.conquestmc.core.dao;

import com.conquestmc.core.model.ConquestPlayer;
import com.conquestmc.core.model.Statistic;
import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.config.RegisterFieldMappers;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Collection;
import java.util.List;

@RegisterFieldMappers({
        @RegisterFieldMapper(Statistic.class),
        @RegisterFieldMapper(ConquestPlayer.class)
})
public interface PlayerDao {

    @SqlUpdate("CREATE TABLE IF NOT EXISTS `players` (uuid VARCHAR(63), name VARCHAR(16), donation_rank VARCHAR(255), conquest_points BIGINT NOT NULL DEFAULT 0, coins BIGINT NOT NULL DEFAULT 0, join_date BIGINT NOT NULL, PRIMARY KEY(uuid));")
    void createPlayerTable();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS `statistics` (player_uuid VARCHAR(63), name VARCHAR(16), value INT NOT NULL DEFAULT 0, PRIMARY KEY (player_uuid, name), FOREIGN KEY (player_uuid) REFERENCES players(uuid));")
    void createStatisticsTable();

    default void createTables() {
        createPlayerTable();
        createStatisticsTable();
    }

    @SqlQuery("SELECT coins FROM players WHERE uuid=:uuid")
    int getCoins(@Bind("uuid") String playerUUID);

    @SqlQuery("SELECT conquest_points FROM players WHERE uuid=:uuid")
    int getConquestPoints(@Bind("uuid") String playerUUID);

    @SqlUpdate("UPDATE players SET conquest_points = conquest_points + :points WHERE uuid=:uuid")
    void setConquestPoints(@Bind("uuid") String playerUUID, @Bind("points") int pointsEarned);

    @SqlUpdate("UPDATE players SET coins = coins + :coins WHERE uuid = :uuid")
    void addCoins(@Bind("uuid") String playerUUID, @Bind("coins") int coinsEarned);

    @SqlUpdate("INSERT INTO players (uuid, name, donation_rank, join_date) VALUES (:uuid, :name, :rank, :joinDate) " +
            "ON DUPLICATE KEY UPDATE uuid = :uuid, name = :name")
    void insertPlayer(@Bind("uuid") String uuid, @Bind("name")String name, @Bind("rank") String rank, @Bind("joinDate") long joinDate);

    @SqlQuery("SELECT `donation_rank` FROM players WHERE uuid=:uuid")
    String getRank(@Bind("uuid") String playerUUID);

    @SqlUpdate("UPDATE players SET donation_rank=:rank WHERE uuid=:uuid")
    void setRank(@Bind("uuid") String playerUUID, @Bind("rank") String rank);

    @SqlQuery("SELECT * FROM players WHERE uuid=:uuid")
    ConquestPlayer getPlayer(@Bind("uuid") String playerUUID);

    @SqlQuery("SELECT name, value FROM statistics WHERE player_uuid = :uuid")
    List<Statistic> getStatistics(@Bind("uuid") String playerUUID);

    @SqlBatch("INSERT INTO statistics (player_uuid, name, value) VALUES (:uuid, :name, :value)" +
            "ON DUPLICATE KEY UPDATE value = value + :value")
    void updateStatistics(@Bind("uuid") String playerUUID, @BindBean Collection<Statistic> statistics);
}
