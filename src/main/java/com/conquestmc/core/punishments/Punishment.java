package com.conquestmc.core.punishments;

import lombok.RequiredArgsConstructor;
import org.bson.Document;

import java.util.UUID;

@RequiredArgsConstructor
public class Punishment {

    private UUID punished;
    private final PunishmentType type;
    private final boolean perm;
    private final int length;

    public Document getDBObject() {
        Document doc = new Document("uuid", punished)
                .append("type", type.name())
                .append("perm", perm)
                .append("length", length);
        return doc;
    }
}
