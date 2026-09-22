package com.github.fbascheper.dj.console.service;

import com.github.fbascheper.dj.console.domain.session.DiscJockey;
import com.github.fbascheper.dj.test.support.DiscJockeyTestObjects;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DjSessionContextTest {

    @Test
    void currentDj_isUnboundByDefault() {
        assertThat(DjSessionContext.CURRENT_DJ.isBound()).isFalse();
    }

    @Test
    void runAs_bindsTheDjForTheDurationOfTheTask() throws Exception {
        var dj = DiscJockeyTestObjects.djVortex();

        var result = DjSessionContext.runAs(dj, () -> DjSessionContext.CURRENT_DJ.get());

        assertThat(result).isSameAs(dj);
    }

    @Test
    void runAs_unbindsDjAfterTaskCompletes() throws Exception {
        var dj = DiscJockeyTestObjects.djVortex();

        DjSessionContext.runAs(dj, () -> null);

        assertThat(DjSessionContext.CURRENT_DJ.isBound()).isFalse();
    }

    @Test
    void runAs_unbindsDjEvenWhenTaskThrows() {
        var dj = DiscJockeyTestObjects.djVortex();

        assertThatThrownBy(() ->
                DjSessionContext.runAs(dj, () -> { throw new RuntimeException("boom"); })
        ).isInstanceOf(RuntimeException.class);

        assertThat(DjSessionContext.CURRENT_DJ.isBound()).isFalse();
    }

    @Test
    void runAs_nestedScopes_innerScopeSeesItsOwnDj() throws Exception {
        var outer = DiscJockeyTestObjects.djVortex();
        var inner = DiscJockey.builder().name("DJ Inner").trackLibrary(List.of()).build();

        var innerResult = DjSessionContext.runAs(outer, () ->
                DjSessionContext.runAs(inner, () -> DjSessionContext.CURRENT_DJ.get()));

        assertThat(innerResult).isSameAs(inner);
    }

    @Test
    void runAs_nestedScopes_outerDjIsRestoredAfterInnerExits() throws Exception {
        var outer = DiscJockeyTestObjects.djVortex();
        var inner = DiscJockey.builder().name("DJ Inner").trackLibrary(List.of()).build();

        var outerResult = DjSessionContext.runAs(outer, () -> {
            DjSessionContext.runAs(inner, () -> null);
            return DjSessionContext.CURRENT_DJ.get();
        });

        assertThat(outerResult).isSameAs(outer);
    }
}
