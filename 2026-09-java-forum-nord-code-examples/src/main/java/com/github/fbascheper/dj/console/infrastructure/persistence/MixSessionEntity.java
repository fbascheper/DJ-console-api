package com.github.fbascheper.dj.console.infrastructure.persistence;

import com.github.fbascheper.dj.console.domain.session.MixSession;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Getter
@Entity
@Table(name = "mix_sessions")
public class MixSessionEntity {

    @Id
    private UUID id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private MixSession session;

    protected MixSessionEntity() {
    }

    public MixSessionEntity(UUID id, MixSession session) {
        this.id = id;
        this.session = session;
    }

}
