package com.github.fbascheper.dj.console.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataMusicLibraryJpaRepository extends JpaRepository<MusicLibraryEntity, UUID> {

    @Query("select m from MusicLibraryEntity m")
    Optional<MusicLibraryEntity> findFirst();

}
