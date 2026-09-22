package com.github.fbascheper.dj.console.service;

import com.github.fbascheper.dj.console.domain.event.CrowdCheered;
import com.github.fbascheper.dj.console.domain.event.CrowdEnergyDropped;
import com.github.fbascheper.dj.console.domain.event.CrowdEvent;
import com.github.fbascheper.dj.console.domain.event.DancefloorEmptied;
import com.github.fbascheper.dj.console.domain.event.DancefloorFilledUp;
import com.github.fbascheper.dj.console.domain.event.RequestFromAudienceReceived;
import com.github.fbascheper.dj.console.domain.session.MixSession;
import com.github.fbascheper.dj.console.domain.session.MixSessionRepository;
import com.github.fbascheper.dj.console.exception.MixSessionNotActiveException;
import com.github.fbascheper.dj.console.exception.MixSessionNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MixSessionServiceImpl implements MixSessionService {

    private final MixSessionRepository repository;
    private final MixSessionUpdatePort mixSessionUpdatePublisher;

    public MixSessionServiceImpl(
            MixSessionRepository repository,
            MixSessionUpdatePort mixSessionUpdatePublisher) {
        this.repository = repository;
        this.mixSessionUpdatePublisher = mixSessionUpdatePublisher;
    }

    @Override
    @Transactional(readOnly = true)
    public MixSession getCurrentSession() {
        return repository.findCurrent()
                .orElseThrow(() -> new MixSessionNotActiveException("active session"));
    }

    @Override
    @Transactional(readOnly = true)
    public MixSession getSessionById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new MixSessionNotFoundException(id.toString()));
    }

    @Override
    @Transactional
    public MixSession applyCrowdCheered(UUID id) {
        var session = getSessionById(id);
        var updated = session.applyEvent(new CrowdCheered(LocalDateTime.now()));
        var saved = repository.save(updated);
        mixSessionUpdatePublisher.publish(saved);
        return saved;
    }

    @Override
    @Transactional
    public MixSession applyCrowdEnergyDropped(UUID id) {
        return applyAndPublish(id, new CrowdEnergyDropped(LocalDateTime.now()));
    }

    @Override
    @Transactional
    public MixSession applyDancefloorEmptied(UUID id) {
        return applyAndPublish(id, new DancefloorEmptied(LocalDateTime.now()));
    }

    @Override
    @Transactional
    public MixSession applyDancefloorFilledUp(UUID id) {
        return applyAndPublish(id, new DancefloorFilledUp(LocalDateTime.now()));
    }

    @Override
    @Transactional
    public MixSession applyRequestFromAudience(UUID id, String trackName) {
        return applyAndPublish(id, new RequestFromAudienceReceived(trackName, LocalDateTime.now()));
    }

    @Override
    @Transactional
    public MixSession applyRecovery(UUID id) {
        // Demo alias: recovery maps to the same domain event as crowd-cheered.
        return applyAndPublish(id, new CrowdCheered(LocalDateTime.now()));
    }

    private MixSession applyAndPublish(UUID id, CrowdEvent event) {
        var session = getSessionById(id);
        var updated = session.applyEvent(event);
        var saved = repository.save(updated);
        mixSessionUpdatePublisher.publish(saved);
        return saved;
    }

}