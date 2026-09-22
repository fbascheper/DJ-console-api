package com.github.fbascheper.dj.console.service;

import com.github.fbascheper.dj.console.config.CrowdVoteProperties;
import com.github.fbascheper.dj.console.exception.CrowdVoteInvalidSlotException;
import com.github.fbascheper.dj.console.exception.CrowdVoteNoVotesException;
import com.github.fbascheper.dj.test.support.MixSessionTestObjects;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CrowdVoteServiceTest {

    private static final UUID SESSION = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

    private static CrowdVoteProperties props() {
        return new CrowdVoteProperties(List.of(
                new CrowdVoteProperties.Choice(0, "Alpha", "Track A"),
                new CrowdVoteProperties.Choice(1, "Beta", "Track B"),
                new CrowdVoteProperties.Choice(2, "Gamma", "Track C")));
    }

    private static CrowdVoteService service() {
        return new CrowdVoteService(props(), mock(MixSessionService.class), mock(CrowdVoteTallyPort.class));
    }

    @Test
    void getTally_startsAtZero() {
        var tally = service().getTally(SESSION);
        assertThat(tally.totalVotes()).isZero();
        assertThat(tally.choices()).extracting(VoteChoice::votes).containsExactly(0, 0, 0);
    }

    @Test
    void castVote_incrementsSlot() {
        var svc = service();
        svc.castVote(SESSION, 1);
        svc.castVote(SESSION, 1);
        svc.castVote(SESSION, 2);
        var tally = svc.getTally(SESSION);
        assertThat(tally.totalVotes()).isEqualTo(3);
        assertThat(tally.choices().get(1).votes()).isEqualTo(2);
        assertThat(tally.choices().get(2).votes()).isEqualTo(1);
    }

    @Test
    void castVote_rejectsInvalidSlot() {
        assertThatThrownBy(() -> service().castVote(SESSION, 3))
                .isInstanceOf(CrowdVoteInvalidSlotException.class);
    }

    @Test
    void reset_clearsTally() {
        var svc = service();
        svc.castVote(SESSION, 0);
        svc.reset(SESSION);
        assertThat(svc.getTally(SESSION).totalVotes()).isZero();
    }

    @Test
    void resolveWinningSlot_prefersHighest_breaksTieByLowestSlot() {
        var svc = service();
        svc.castVote(SESSION, 1);
        svc.castVote(SESSION, 1);
        svc.castVote(SESSION, 2);
        svc.castVote(SESSION, 2);
        svc.castVote(SESSION, 0);
        var tally = svc.getTally(SESSION);
        assertThat(svc.resolveWinningSlot(tally)).isEqualTo(1);
    }

    @Test
    void resolveWinningSlot_allZero_returnsZero() {
        var svc = service();
        var tally = svc.getTally(SESSION);
        assertThat(svc.resolveWinningSlot(tally)).isZero();
    }

    @Test
    void applyCrowdVoteWinner_appliesWinnerAndResetsVotes() {
        var mixSessionService = mock(MixSessionService.class);
        var publisher = mock(CrowdVoteTallyPort.class);
        var svc = new CrowdVoteService(props(), mixSessionService, publisher);
        var expectedSession = MixSessionTestObjects.vortexEmptyMixSession();

        when(mixSessionService.applyRequestFromAudience(SESSION, "Track B")).thenReturn(expectedSession);

        svc.castVote(SESSION, 1);
        svc.castVote(SESSION, 1);
        svc.castVote(SESSION, 2);

        var result = svc.applyCrowdVoteWinner(SESSION);

        assertThat(result).isEqualTo(expectedSession);
        assertThat(svc.getTally(SESSION).totalVotes()).isZero();
        verify(publisher).publish(svc.getTally(SESSION));
    }

    @Test
    void applyCrowdVoteWinner_throwsWhenNoVotes() {
        assertThatThrownBy(() -> service().applyCrowdVoteWinner(SESSION))
                .isInstanceOf(CrowdVoteNoVotesException.class);
    }
}
