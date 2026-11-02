package com.github.fbascheper.dj.console.bootstrap;

import com.github.fbascheper.dj.console.domain.library.Artist;
import com.github.fbascheper.dj.console.domain.library.MusicLibrary;
import com.github.fbascheper.dj.console.domain.library.Song;
import com.github.fbascheper.dj.console.domain.library.Track;
import com.github.fbascheper.dj.console.infrastructure.persistence.MusicLibraryEntity;
import com.github.fbascheper.dj.console.infrastructure.persistence.SpringDataMusicLibraryJpaRepository;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class MusicLibrarySmartLoader implements SmartLifecycle {

    private final SpringDataMusicLibraryJpaRepository repo;
    private final ObjectMapper mapper;
    private volatile boolean running = false;

    public MusicLibrarySmartLoader(SpringDataMusicLibraryJpaRepository repo, ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public void start() {
        try {
            var resolver = new PathMatchingResourcePatternResolver();

            // Load artists
            List<Artist> artists = List.of();
            var aFiles = resolver.getResources("classpath:seed/library/artists.json");
            if (aFiles.length > 0) {
                try (var in = aFiles[0].getInputStream()) {
                    artists = mapper.readValue(in,
                            mapper.getTypeFactory().constructCollectionType(List.class, Artist.class));
                }
            }

            // Load songs
            List<Song> songs = List.of();
            var sFiles = resolver.getResources("classpath:seed/library/songs.json");
            if (sFiles.length > 0) {
                try (var in = sFiles[0].getInputStream()) {
                    songs = mapper.readValue(in,
                            mapper.getTypeFactory().constructCollectionType(List.class, Song.class));
                }
            }

            // Load tracks
            List<Track> tracks = List.of();
            var tFiles = resolver.getResources("classpath:seed/library/tracks.json");
            if (tFiles.length > 0) {
                try (var in = tFiles[0].getInputStream()) {
                    tracks = mapper.readValue(in,
                            mapper.getTypeFactory().constructCollectionType(List.class, Track.class));
                }
            }

            // Basic referential checks
            var artistIds = artists.stream().map(a -> a.id().value()).collect(Collectors.toSet());
            for (var s : songs) {
                if (!artistIds.contains(s.artist().id().value())) {
                    throw new IllegalStateException("Song references unknown artist: " + s.title());
                }
            }
            var songIds = songs.stream().map(s -> s.id().value()).collect(Collectors.toSet());
            for (var t : tracks) {
                if (!songIds.contains(t.song().id().value())) {
                    throw new IllegalStateException("Track references unknown song: " + t.song().title());
                }
            }

            var library = MusicLibrary.builder()
                    .artists(artists)
                    .songs(songs)
                    .tracks(tracks)
                    .build();

            repo.save(new MusicLibraryEntity(
                    UUID.fromString("00000000-0000-0000-0000-000000000001"),
                    library));

            running = true;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to load MusicLibrary", ex);
        }
    }

    @Override
    public void stop() {
        // Nothing to release; mark stopped.
        running = false;
    }

    @Override
    public void stop(Runnable callback) {
        // Non-blocking; call the callback immediately after marking stopped
        running = false;
        callback.run();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    /**
     * Runs BEFORE MixSession loader
     */
    @Override
    public int getPhase() {
        return 10;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }
}