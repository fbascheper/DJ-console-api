package com.github.fbascheper.dj.console.rest;

import module java.base;      // URI, Executors, CompletableFuture, IntStream, ...
import module java.net.http;  // HttpClient, HttpRequest, HttpResponse  (JEP 511 / Java 25)

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demonstrates {@link HttpClient} (Java 11, Item 59: know and use the libraries).
 *
 * <p>Before Java 11 you reached for Apache HttpClient or OkHttp.
 * Since Java 11 the JDK ships a fluent, non-blocking HTTP/2 client in {@code java.net.http}.
 * This test starts the full application on a random port and calls the REST endpoint
 * using only the standard library — no third-party HTTP client dependency needed.
 *
 * <p>Java 25 note: imports use module import declarations (JEP 511, standard since Java 25;
 * JEP 494 was the second preview in Java 24):
 * {@code import module java.net.http} replaces three individual type imports.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DjConsoleClientDemoTest {

    @LocalServerPort
    private int port;

    @Test
    void getCurrentSession_withJdkHttpClient() throws Exception {
        var client = HttpClient.newHttpClient();

        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/sessions/current"))
                .header("Accept", "application/json")
                .GET()
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body())
                .contains("sessionId")
                .contains("discJockeyName")
                .contains("playedTracks")
                .contains("REST");
    }

    @Test
    void getCurrentSession_httpVersion() throws Exception {
        var client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();

        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/sessions/current"))
                .header("Accept", "application/json")
                .GET()
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        // HTTP/2 is preferred but Tomcat negotiates HTTP/1.1 without TLS
        assertThat(response.version()).isIn(HttpClient.Version.HTTP_1_1, HttpClient.Version.HTTP_2);
    }

    /**
     * Demonstrates async HTTP with virtual threads (Java 21+, Item 59).
     *
     * <p>{@code sendAsync} returns a {@link CompletableFuture} immediately; the virtual-thread
     * executor ensures each response callback runs on a cheap virtual thread rather than
     * blocking a platform thread. Three requests are fired concurrently and all results
     * are collected before asserting — no thread coordination code needed.
     */
    @Test
    void getCurrentSession_asyncWithVirtualThreads() {
        var url = URI.create("http://localhost:" + port + "/api/sessions/current");

        try (var client = HttpClient.newBuilder()
                .executor(Executors.newVirtualThreadPerTaskExecutor())
                .build()) {

            var request = HttpRequest.newBuilder()
                    .uri(url)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            var futures = IntStream.range(0, 3)
                    .mapToObj(_ -> client.sendAsync(request, HttpResponse.BodyHandlers.ofString()))
                    .toList();

            var responses = futures.stream()
                    .map(CompletableFuture::join)
                    .toList();

            assertThat(responses).allSatisfy(response -> {
                assertThat(response.statusCode()).isEqualTo(200);
                assertThat(response.body()).contains("sessionId");
            });
        }
    }
}
