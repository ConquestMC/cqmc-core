package com.conquestmc.core.config;

import lombok.Data;

@Data
public class AccessCode {

    private String codeName;
    private int successfulUses;
    private int failedUses;
    private int limit;
    private boolean active;
}
