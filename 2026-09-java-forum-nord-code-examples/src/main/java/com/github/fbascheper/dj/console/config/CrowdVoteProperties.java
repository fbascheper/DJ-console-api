package com.github.fbascheper.dj.console.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Comparator;
import java.util.List;

/**
 * The three QR vote options: label for UI / QR pages, {@code trackName} must match
 * {@link com.github.fbascheper.dj.console.domain.session.DiscJockey#findTrackByTitle(String)} (seed library).
 */
@ConfigurationProperties(prefix = "dj.crowd-vote")
public record CrowdVoteProperties(List<Choice> choices) {

    public CrowdVoteProperties {
        if (choices == null || choices.size() != 3) {
            throw new IllegalStateException("dj.crowd-vote.choices must contain exactly 3 entries");
        }
        var slots = choices.stream().mapToInt(Choice::slot).sorted().toArray();
        if (slots[0] != 0 || slots[1] != 1 || slots[2] != 2) {
            throw new IllegalStateException("dj.crowd-vote.choices slots must be 0, 1, and 2");
        }
        choices = List.copyOf(choices.stream().sorted(Comparator.comparingInt(Choice::slot)).toList());
    }

    public record Choice(int slot, String label, String trackName) {
    }
}
