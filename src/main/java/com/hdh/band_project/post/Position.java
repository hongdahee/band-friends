package com.hdh.band_project.post;

public enum Position {
    GUITAR("기타"),
    BASS("베이스"),
    VOCAL("보컬"),
    DRUM("드럼"),
    KEYBOARD("키보드");

    private final String displayName;

    Position(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
