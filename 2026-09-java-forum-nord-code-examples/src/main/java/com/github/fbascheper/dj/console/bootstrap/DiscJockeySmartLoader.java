package com.github.fbascheper.dj.console.bootstrap;

import com.github.fbascheper.dj.console.bootstrap.dto.DiscJockeySeed;
import com.github.fbascheper.dj.console.domain.MusicLibraryLookup;
import com.github.fbascheper.dj.console.domain.session.DiscJockey;
import com.github.fbascheper.dj.console.domain.session.SessionTrack;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;


/**
 * Loads {@link DiscJockey} personal libraries from {@code classpath:seed/discjockeys/*.json}
 * and registers them in the {@link DiscJockeyRegistry}.
 *
 * <p>Runs at phase 15 — after {@link MusicLibrarySmartLoader} (phase 10) so that track IDs
 * can be resolved via {@link MusicLibraryLookup}, and before {@link MixSessionSmartLoader}
 * (phase 20) which needs the populated registry.
 */
@Component
public class DiscJockeySmartLoader implements SmartLifecycle {

    private final MusicLibraryLookup lookup;
    private final DiscJockeyRegistry registry;
    private final ObjectMapper mapper;
    private volatile boolean running = false;

    public DiscJockeySmartLoader(MusicLibraryLookup lookup,
                                 DiscJockeyRegistry registry,
                                 ObjectMapper mapper) {
        this.lookup = lookup;
        this.registry = registry;
        this.mapper = mapper;
    }

    @Override
    public void start() {
        try {
            var resolver = new PathMatchingResourcePatternResolver();
            var files = resolver.getResources("classpath:seed/discjockeys/*.json");

            for (var resource : files) {
                try (var in = resource.getInputStream()) {
                    var seed = mapper.readValue(in, DiscJockeySeed.class);
                    registry.register(buildDiscJockey(seed));
                }
            }

            running = true;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed loading DiscJockeys", ex);
        }
    }

    private DiscJockey buildDiscJockey(DiscJockeySeed seed) {
        var trackLibrary = seed.trackIds().stream()
                .map(tid -> lookup.findTrackById(tid.value())
                        .orElseThrow(() -> new IllegalStateException(
                                "DJ '" + seed.name() + "': unknown trackId " + tid.value())))
                .map(SessionTrack::fromLibrary)
                .toList();
        return DiscJockey.builder()
                .name(seed.name())
                .trackLibrary(trackLibrary)
                .build();
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public void stop(Runnable callback) {
        running = false;
        callback.run();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return 15;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }
}
