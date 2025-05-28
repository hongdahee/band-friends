package com.hdh.band_project.band;

public enum MemberRole {
    LEADER("리더"),
    MEMBER("멤버"),
    STAFF("스탭");

    private final String displayName;

    MemberRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
