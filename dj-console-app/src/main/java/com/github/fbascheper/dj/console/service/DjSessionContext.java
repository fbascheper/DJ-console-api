package com.github.fbascheper.dj.console.service;

import com.github.fbascheper.dj.console.domain.session.DiscJockey;

import java.util.concurrent.Callable;

/**
 * Scoped request context carrying the active {@link DiscJockey} for the current scope.
 *
 * <p>Demonstrates {@link ScopedValue} (JEP 506, standard in Java 25; JEP 487 was the
 * fourth preview in Java 24) as the safe
 * replacement for {@link ThreadLocal} when using virtual threads:
 * <ul>
 *   <li>No manual {@code remove()} — the binding is automatically released when
 *       {@link #runAs} exits, even on exception.</li>
 *   <li>Immutable within the scope — a nested {@code runAs} creates a new inner
 *       binding without affecting the outer one.</li>
 *   <li>Safe with virtual threads — no risk of cross-request leakage.</li>
 * </ul>
 *
 * <p>Contrast with the {@code ThreadLocal} equivalent:
 * <pre>{@code
 * // ThreadLocal — requires manual cleanup; forgets remove() → memory / correctness bug
 * static final ThreadLocal<DiscJockey> DJ = new ThreadLocal<>();
 * DJ.set(dj);
 * try { doWork(); } finally { DJ.remove(); }
 *
 * // ScopedValue — auto-scoped, no finally needed (Java 25, JEP 506)
 * DjSessionContext.runAs(dj, this::doWork);
 * }</pre>
 */
public final class DjSessionContext {

    public static final ScopedValue<DiscJockey> CURRENT_DJ = ScopedValue.newInstance();

    private DjSessionContext() {}

    /**
     * Runs {@code task} with {@code dj} bound to {@link #CURRENT_DJ} for the duration.
     * The binding is released automatically when the task returns or throws.
     */
    public static <T> T runAs(DiscJockey dj, Callable<T> task) throws Exception {
        return ScopedValue.where(CURRENT_DJ, dj).call(task::call);
    }
}
