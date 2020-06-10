package com.conquestmc.core.punishments;

import com.conquestmc.core.util.TimeUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.Document;

import java.util.UUID;

@Getter
@Setter
public class Punishment {

    private final long issued;
    private final UUID punished;
    private final UUID punishmentId;
    private final PunishmentType type;
    private boolean perm;
    private long activeUntil;
    private int severity;

    public Document getDBObject() {
        return new Document("uuid", punished)
                .append("punishmentId", punishmentId)
                .append("type", type.name())
                .append("perm", perm)
                .append("activeUntil", activeUntil)
                .append("severity", severity)
                .append("issued", issued);
    }

    public Punishment(UUID punished, UUID punishmentId, PunishmentType type) {
        this.punished = punished;
        this.punishmentId = punishmentId;
        this.type = type;
        this.issued = System.currentTimeMillis();
    }

    public Punishment(Document doc) {
        this.punished = UUID.fromString(doc.getString("uuid"));
        this.punishmentId = UUID.fromString(doc.getString("punishmentId"));
        this.type = PunishmentType.valueOf(doc.getString("type"));
        this.perm = (boolean) doc.get("perm");
        this.activeUntil = doc.getLong("activeUntil");
        this.severity = doc.getInteger("severity");
        this.issued = doc.getLong("issued");
    }

    public String getTimeLeftVisual() {
        //if end = 1000 and it is currently 500. it is end - current.
        long remaining = activeUntil - System.currentTimeMillis();
        return TimeUtil.formatTimeToFormalDate(remaining);
    }
}
