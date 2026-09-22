package com.github.fbascheper.dj.console.bootstrap;

import com.github.fbascheper.dj.console.bootstrap.dto.MixSessionSeed;
import com.github.fbascheper.dj.console.domain.MusicLibraryLookup;
import com.github.fbascheper.dj.console.domain.session.DiscJockey;
import com.github.fbascheper.dj.console.domain.session.MixSession;
import com.github.fbascheper.dj.console.domain.session.SessionTrack;
import com.github.fbascheper.dj.console.infrastructure.persistence.MixSessionEntity;
import com.github.fbascheper.dj.console.infrastructure.persistence.SpringDataMixSessionJpaRepository;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;


@Component
public class MixSessionSmartLoader implements SmartLifecycle {

    private final SpringDataMixSessionJpaRepository repo;
    private final MusicLibraryLookup lookup;
    private final ObjectMapper mapper;
    private volatile boolean running = false;

    public MixSessionSmartLoader(SpringDataMixSessionJpaRepository repo,
                                 MusicLibraryLookup lookup,
                                 ObjectMapper mapper) {
        this.repo = repo;
        this.lookup = lookup;
        this.mapper = mapper;
    }

    @Override
    public void start() {
        try {
            var resolver = new PathMatchingResourcePatternResolver();
            var files = resolver.getResources("classpath:seed/sessions/*.json");
            if (files.length == 0) {
                running = true; // no sessions to load but startup is OK
                return;
            }

            for (var resource : files) {
                try (var in = resource.getInputStream()) {
                    MixSessionSeed seed = mapper.readValue(in, MixSessionSeed.class);

                    var resolved = seed.trackIds().stream()
                            .map(tid -> lookup.findTrackById(tid.value())
                                    .orElseThrow(() -> new IllegalStateException("Unknown trackId: " + tid.value())))
                            .map(SessionTrack::fromLibrary)
                            .toList();

                    var djLibrary = lookup.allSessionTracks();
                    var dj = new DiscJockey(seed.dj().name(), djLibrary);

                    var session = MixSession.builder()
                            .id(seed.id())
                            .dj(dj)
                            .tracks(resolved)
                            .crowdEvents(seed.crowdEvents())
                            .build();

                    repo.save(new MixSessionEntity(session.id().value(), session));
                }
            }

            running = true;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed loading MixSessions", ex);
        }
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

    /**
     * Runs AFTER MusicLibrary loader
     */
    @Override
    public int getPhase() {
        return 20;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }
}