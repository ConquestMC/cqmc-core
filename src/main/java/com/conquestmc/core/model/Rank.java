package com.conquestmc.core.model;

public enum Rank {
    TUTORIAL_1(2499, "&7Noobie"),
    EXPERIENCE_1(9999, "&7Still Learning"),
    EXPERIENCE_2(24999, "&7Needs Practice"),
    COMP_1(99999, "&3Elite"),
    COMP_2(199999, "&3Elite II"),
    COMP_3(299999, "&3Elite III"),
    COMP_4(399999, "&6Champion I"),
    COMP_5(499999, "&6Champion II"),
    COMP_6(599999, "&6Champion III"),
    COMP_7(699999, "&cMASTER I"),
    COMP_8(799999, "&cMASTER II"),
    COMP_9(950000, "&cMASTER III"),
    COMP_10(1000000, "&6&lGrand Master")
    ;
    int conquestPoints;
    String prefix;

    Rank(int conquestPoints, String prefix) {
        this.conquestPoints = conquestPoints;
        this.prefix = prefix;
    }
}
