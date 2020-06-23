package com.conquestmc.core.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Data
public class Trophy {
    private int tier = 1;
    private final String displayName;
    private final List<String> description;
}
