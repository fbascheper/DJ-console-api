package com.github.fbascheper.dj.console.infrastructure.persistence;

import com.github.fbascheper.dj.console.domain.library.MusicLibrary;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "music_library")
@Getter
public class MusicLibraryEntity {

    @Id
    private UUID id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private MusicLibrary library;

    protected MusicLibraryEntity() {
    }

    public MusicLibraryEntity(UUID id, MusicLibrary library) {
        this.id = id;
        this.library = library;
    }

}
