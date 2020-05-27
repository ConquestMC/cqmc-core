package com.conquestmc.core.config;

import lombok.Data;

import java.util.List;

@Data
public class MainConfig {
    private final List<String> bannedWords;
    private List<AccessCode> accessCodes;


}
