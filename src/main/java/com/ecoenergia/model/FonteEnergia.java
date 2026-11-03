package com.ecoenergia.model;

public enum FonteEnergia {
    REDE(false),
    SOLAR(true),
    EOLICA(true),
    BIOMASSA(true),
    GERADOR_DIESEL(false);

    private final boolean renovavel;

    FonteEnergia(boolean renovavel) { this.renovavel = renovavel; }

    public boolean isRenovavel() { return renovavel; }
}
