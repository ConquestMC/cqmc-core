package com.conquestmc.core.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Statistic {


    private String name;
    private int value;

    public Statistic(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public Statistic() {
        //Bean Mapper
    }
}
