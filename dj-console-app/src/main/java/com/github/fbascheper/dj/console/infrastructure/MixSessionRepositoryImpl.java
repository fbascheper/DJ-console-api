package com.github.fbascheper.dj.console.infrastructure;

import com.github.fbascheper.dj.console.domain.MusicLibraryLookup;
import com.github.fbascheper.dj.console.domain.session.DiscJockey;
import com.github.fbascheper.dj.console.domain.session.MixSession;
import com.github.fbascheper.dj.console.domain.session.MixSessionRepository;
import com.github.fbascheper.dj.console.infrastructure.persistence.MixSessionEntity;
import com.github.fbascheper.dj.console.infrastructure.persistence.SpringDataMixSessionJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class MixSessionRepositoryImpl implements MixSessionRepository {

    private final SpringDataMixSessionJpaRepository jpa;
    private final MusicLibraryLookup musicLibraryLookup;

    public MixSessionRepositoryImpl(
            SpringDataMixSessionJpaRepository jpa,
            MusicLibraryLookup musicLibraryLookup) {
        this.jpa = jpa;
        this.musicLibraryLookup = musicLibraryLookup;
    }

    @Override
    public Optional<MixSession> findCurrent() {
        // FIXME: Example policy: "current" is the most recently updated/created session.
        // TODO: Implement a real policy that matches your domain (e.g., a flag on the entity).
        return jpa.findAll()
                .stream()
                // naive: last in list; replace with real criteria
                .reduce((a, b) -> b)
                .map(MixSessionEntity::getSession)
                .map(this::ensureDjTrackLibrary);
    }

    @Override
    public Optional<MixSession> findById(UUID id) {
        return jpa.findById(id).map(MixSessionEntity::getSession).map(this::ensureDjTrackLibrary);
    }

    @Override
    public MixSession save(MixSession session) {
        jpa.save(new MixSessionEntity(session.id().value(), session));
        return session;
    }

    /**
     * JSON seed only stored DJ name; older rows may deserialize with {@code trackLibrary == null}.
     */
    private MixSession ensureDjTrackLibrary(MixSession session) {
        if (session.dj().trackLibrary() != null) {
            return session;
        }
        var dj = new DiscJockey(session.dj().name(), musicLibraryLookup.allSessionTracks());
        return new MixSession(session.id(), dj, session.tracks(), session.crowdEvents());
    }
}