package com.conquestmc.core.punishments;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.Document;

import java.util.UUID;

@Getter
@Setter
public class Punishment {

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
                .append("severity", severity);
    }

    public Punishment(UUID punished, UUID punishmentId, PunishmentType type) {
        this.punished = punished;
        this.punishmentId = punishmentId;
        this.type = type;
    }

    public Punishment(Document doc) {
        this.punished = UUID.fromString(doc.getString("uuid"));
        this.punishmentId = UUID.fromString(doc.getString("punishmentId"));
        this.type = PunishmentType.valueOf(doc.getString("type"));
        this.perm = (boolean) doc.get("perm");
        this.activeUntil = doc.getLong("activeUntil");
        this.severity = doc.getInteger("severity");
    }
}
