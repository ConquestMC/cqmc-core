package com.conquestmc.core.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class FriendRequest {

    private final UUID from, to;
    public boolean accepted = false;

    public void accept() {
        this.accepted = true;
    }
}
