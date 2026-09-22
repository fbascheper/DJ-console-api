package com.github.fbascheper.dj.test.support;

import com.github.fbascheper.dj.console.domain.library.CuePoint;

import java.time.Duration;
import java.util.List;

public enum CuePointTestObjects {
    CUE_POINT_START("start", Duration.ZERO),
    CUE_POINT_AFTER_1000_MS("After 1 second", Duration.ofMillis(1_000)),
    CUE_POINT_AFTER_20_S("After 20 seconds", Duration.ofMillis(20_000)),
    CUE_POINT_AFTER_25_S("After 25 seconds", Duration.ofMillis(25_000));

    private CuePoint instance;

    CuePointTestObjects(String label, Duration elapsedTime) {
        this.instance = CuePoint.builder()
                .label(label)
                .elapsedTime(elapsedTime)
                .build();

    }

    public CuePoint getInstance() {
        return instance;
    }

    public static List<CuePoint> defaultCuePointList() {
        return List.of(
                CuePoint.builder().label("start").elapsedTime(Duration.ofMillis(0)).build()
                , CuePoint.builder().label("after_20_secs").elapsedTime(Duration.ofMillis(20_000)).build()
                , CuePoint.builder().label("after_25_secs").elapsedTime(Duration.ofMillis(25_000)).build()
        );
    }
}
