package com.github.fbascheper.dj.console.domain.session;

import com.github.fbascheper.dj.console.domain.util.ValidatingBuilder;
import lombok.Singular;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A record of a dance event, where the DJ will perform one or more @{link MixSession}-instances.
 */
@lombok.Builder(toBuilder = true, buildMethodName = "internalBuild", builderClassName = "Builder")
public record DanceEvent(

        String name,

        String location,

        LocalDateTime date,

        int expectedAttendees,

        @Singular
        List<MixSession> mixSessions
) {

    @SuppressWarnings("unused")
    public static class Builder implements ValidatingBuilder<DanceEvent> {
        // lombok will generate the constructor, setters, build method, etc.
    }

}