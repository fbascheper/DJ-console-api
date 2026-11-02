package com.github.fbascheper.dj.test.support;

import com.github.fbascheper.dj.console.domain.library.Song;
import lombok.Getter;

import static com.github.fbascheper.dj.test.support.ArtistTestObjects.*;

@Getter
public enum SongTestObjects {

    // ZEPH∆R songs
    NEON_REQUIEM(ZEPHAR, "Neon Requiem"),
    SKYFALL_PROTOCOL(ZEPHAR, "Skyfall Protocol"),
    ECLIPSE_MODE(ZEPHAR, "Eclipse Mode"),

    // NØVA BLISS songs
    VELVET_SURGE(NOVA_BLISS, "Velvet Surge"),
    HYPERDRIVE_KISS(NOVA_BLISS, "Hyperdrive Kiss"),
    MIDNIGHT_OVERRIDE(NOVA_BLISS, "Midnight Override"),
    DIGITAL_FEVER(NOVA_BLISS, "Digital Fever"),
    PRISM_HEART(NOVA_BLISS, "Prism Heart"),
    CRYSTAL_VENOM(NOVA_BLISS, "Crystal Venom"),
    PHANTOM_GLOW(NOVA_BLISS, "Phantom Glow"),
    ELECTRIC_SHRINE(NOVA_BLISS, "Electric Shrine"),
    PULSE_ECHO(NOVA_BLISS, "Pulse Echo"),
    VOLTAGE_BLOOM(NOVA_BLISS, "Voltage Bloom"),
    NEON_VEIL(NOVA_BLISS, "Neon Veil"),

    // AXIOM songs
    AFTERBURN(AXIOM, "Afterburn"),
    SKYLINE_DROP(AXIOM, "Skyline Drop"),
    RIFT_SIGNAL(AXIOM, "Rift Signal"),
    SOLAR_DRIFT(AXIOM, "Solar Drift"),
    ZERO_GRAVITY(AXIOM, "Zero Gravity"),
    PHASE_SHIFT(AXIOM, "Phase Shift"),
    THUNDER_SYNC(AXIOM, "Thunder Sync"),
    WAVE_COLLAPSE(AXIOM, "Wave Collapse"),
    GAMMA_RUSH(AXIOM, "Gamma Rush"),
    INFINITY_LOOP(AXIOM, "Infinity Loop"),

    // LUX FADER songs
    GLITCH_HORIZON(LUX_FADER, "Glitch Horizon"),
    BASS_REACTOR(LUX_FADER, "Bass Reactor"),
    CYBER_SUNRISE(LUX_FADER, "Cyber Sunrise"),
    NEON_CASCADE(LUX_FADER, "Neon Cascade"),
    DROP_ZONE(LUX_FADER, "Drop Zone"),
    LASER_BLOOM(LUX_FADER, "Laser Bloom"),
    STROBE_KINGDOM(LUX_FADER, "Strobe Kingdom"),
    FLUX_CIRCUIT(LUX_FADER, "Flux Circuit"),

    // KRØM songs
    CHROME_THUNDER(KROM, "Chrome Thunder"),
    OVERDRIVE_RITUAL(KROM, "Overdrive Ritual"),
    VOLTAGE_HIGHWAY(KROM, "Voltage Highway"),
    STEEL_MIRAGE(KROM, "Steel Mirage"),
    TURBO_GHOST(KROM, "Turbo Ghost"),
    BURNOUT_ECHO(KROM, "Burnout Echo"),
    WARP_ENGINE(KROM, "Warp Engine"),
    IRON_PULSE(KROM, "Iron Pulse"),
    NITRO_FADE(KROM, "Nitro Fade"),
    PHANTOM_GEAR(KROM, "Phantom Gear");

    private Song instance;

    SongTestObjects(ArtistTestObjects artists, String title) {
        this.instance = Song.builder()
                .artist(artists.getInstance())
                .title(title)
                .build();
    }

}
