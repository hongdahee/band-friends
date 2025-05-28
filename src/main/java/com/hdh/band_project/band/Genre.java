package com.hdh.band_project.band;

public enum Genre {
    ALL("전체"),
    ROCK("락"),
    POP("팝"),
    JAZZ("재즈"),
    METAL("메탈"),
    K_POP("K-POP"),
    INDIE("인디"),
    J_POP("J-POP"),
    BALLAD("발라드"),
    CLASSICAL("클래식"),
    HIPHOP("힙합"),
    EDM("EDM");

    private final String displayName;

    Genre(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
