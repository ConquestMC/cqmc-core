package com.conquestmc.core.player;

import java.util.List;

public class StaffRank extends Rank {
    public StaffRank(String name) {
        super(name);
    }

    public StaffRank(String name, List<Rank> inherits) {
        super(name, inherits);
    }
}
