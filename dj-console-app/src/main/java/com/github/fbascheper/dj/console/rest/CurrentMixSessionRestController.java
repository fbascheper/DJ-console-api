package com.github.fbascheper.dj.console.rest;

import com.github.fbascheper.dj.console.rest.dto.CurrentMixSessionRestResponse;
import com.github.fbascheper.dj.console.service.MixSessionService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Legacy-style REST read for the "current session" resource.
 * <p>
 * Included for conference demos: one endpoint, one bloated JSON shape —
 * versus GraphQL where each client requests only the fields it needs.
 * <p>
 * {@code @CrossOrigin} allows the Reveal.js presentation (served on any localhost port)
 * to fetch this endpoint inline via {@code fetch()} without opening a new browser tab.
 */
@Slf4j
@CrossOrigin(originPatterns = { "http://localhost:*", "http://127.0.0.1:*", "https://*.ngrok-free.app", "https://*.ngrok-free.dev" })
@RestController
@RequestMapping(path = "/api/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
public class CurrentMixSessionRestController {

    private final MixSessionService mixSessionService;

    public CurrentMixSessionRestController(MixSessionService mixSessionService) {
        this.mixSessionService = mixSessionService;
    }

    @GetMapping("/current")
    public CurrentMixSessionRestResponse currentSession() {
        log.info("REST GET /api/sessions/current");
        var session = mixSessionService.getCurrentSession();
        var response = MixSessionRestMapper.toFatResponse(session);
        log.debug("REST GET /api/sessions/current -> session id={}, status={}, tracks={}",
                session.id().value(), session.getStatus(), session.tracks().size());
        return response;
    }
}
