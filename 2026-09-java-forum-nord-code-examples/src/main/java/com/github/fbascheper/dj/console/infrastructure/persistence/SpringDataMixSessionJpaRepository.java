package com.github.fbascheper.dj.console.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataMixSessionJpaRepository
        extends JpaRepository<MixSessionEntity, UUID> {
}