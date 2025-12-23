package com.hdh.band_project.band;

public class BandCountDto {
    private Band band;
    private long newApplyCount;

    public BandCountDto(Band band, long newApplyCount) {
        this.band = band;
        this.newApplyCount = newApplyCount;
    }

    public Band getBand() {
        return band;
    }

    public long getNewApplyCount() {
        return newApplyCount;
    }
}
