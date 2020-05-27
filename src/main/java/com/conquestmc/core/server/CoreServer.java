package com.conquestmc.core.server;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class CoreServer {
    private final String name;
    private final String ip, port;

    private List<UUID> players = Lists.newArrayList();
}
