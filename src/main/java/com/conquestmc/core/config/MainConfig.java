package com.conquestmc.core.config;

import com.conquestmc.core.server.ServerType;
import lombok.Data;

import java.util.List;

@Data
public class MainConfig {

    private final List<String> bannedWords;
    private String serverAddress;
    private ServerType type;
}
