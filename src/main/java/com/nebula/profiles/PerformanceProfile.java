package com.nebula.profiles;

public enum PerformanceProfile {
    LOW_END("Low End", "Máxima performance, qualidade reduzida"),
    BALANCED("Balanced", "Equilíbrio entre qualidade e performance"),
    QUALITY("Quality", "Melhor qualidade visual possível");

    private final String displayName;
    private final String description;

    PerformanceProfile(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
