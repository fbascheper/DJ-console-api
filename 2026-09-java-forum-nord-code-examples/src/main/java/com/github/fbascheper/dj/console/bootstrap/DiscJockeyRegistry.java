package com.github.fbascheper.dj.console.bootstrap;

import com.github.fbascheper.dj.console.domain.session.DiscJockey;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory registry of {@link DiscJockey} instances loaded from seed files at startup.
 *
 * <p>Populated by {@link DiscJockeySmartLoader} (phase 15) and consumed by
 * {@link MixSessionSmartLoader} (phase 20).
 */
@Component
public class DiscJockeyRegistry {

    private final Map<String, DiscJockey> byName = new HashMap<>();

    void register(DiscJockey dj) {
        byName.put(dj.name(), dj);
    }

    public Optional<DiscJockey> findByName(String name) {
        return Optional.ofNullable(byName.get(name));
    }
}
