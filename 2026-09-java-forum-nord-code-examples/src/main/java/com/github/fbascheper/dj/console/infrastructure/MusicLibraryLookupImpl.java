package com.github.fbascheper.dj.console.infrastructure;

import com.github.fbascheper.dj.console.domain.MusicLibraryLookup;
import com.github.fbascheper.dj.console.domain.library.MusicLibrary;
import com.github.fbascheper.dj.console.domain.library.Track;
import com.github.fbascheper.dj.console.domain.session.SessionTrack;
import com.github.fbascheper.dj.console.infrastructure.persistence.SpringDataMusicLibraryJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Infrastructure adapter to look up Tracks in the persisted MusicLibrary.
 */
@Component
public class MusicLibraryLookupImpl implements MusicLibraryLookup {

    private final SpringDataMusicLibraryJpaRepository repository;

    public MusicLibraryLookupImpl(SpringDataMusicLibraryJpaRepository repository) {
        this.repository = repository;
    }

    private MusicLibrary library() {
        return repository.findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "MusicLibrary not loaded before MixSession loader ran"))
                .getLibrary();
    }

    @Override
    public Optional<Track> findTrackById(UUID trackId) {
        return library().tracks().stream()
                .filter(t -> t.id().value().equals(trackId))
                .findFirst();
    }

    @Override
    public boolean existAllTrackIds(List<UUID> trackIds) {
        var existing = library().tracks().stream()
                .map(t -> t.id().value())
                .collect(Collectors.toSet());

        return existing.containsAll(trackIds);
    }

    @Override
    public List<SessionTrack> allSessionTracks() {
        return library().tracks().stream()
                .map(SessionTrack::fromLibrary)
                .toList();
    }
}