package dev.gainsmilk.keepsprint.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    @Test
    void defaultsHaveCorrectValues() {
        Config cfg = new Config();
        assertTrue(cfg.keepSprintEnabled);
        assertFalse(cfg.omniSprintEnabled);
        assertEquals(1.0, cfg.omniSprintMultiplier, 1e-9);
        assertFalse(cfg.fastPlaceEnabled);
        assertFalse(cfg.blockReachEnabled);
        assertEquals(6.0, cfg.blockReachDistance, 1e-9);
        assertFalse(cfg.timerEnabled);
        assertEquals(1.0, cfg.timerMultiplier, 1e-9);
    }

    @Test
    void saveWritesAllCurrentKeys(@TempDir Path tmp) throws IOException {
        Path p = tmp.resolve("cfg.properties");
        Config cfg = new Config();
        cfg.save(p);

        Properties props = readProps(p);
        List<String> expected = Arrays.asList(
                "keepSprintEnabled", "omniSprintEnabled", "omniSprintMultiplier",
                "fastPlaceEnabled", "blockReachEnabled", "blockReachDistance",
                "timerEnabled", "timerMultiplier");
        for (String key : expected) {
            assertTrue(props.containsKey(key), "missing key: " + key);
        }
        assertEquals(8, expected.size());
    }

    @Test
    void loadReturnsDefaultsWhenFileDoesNotExist(@TempDir Path tmp) {
        Path p = tmp.resolve("missing.properties");
        assertFalse(Files.exists(p));

        Config cfg = Config.load(p);

        assertTrue(Files.exists(p), "load should auto-create the file");
        assertTrue(cfg.keepSprintEnabled);
        assertEquals(1.0, cfg.omniSprintMultiplier, 1e-9);
        assertEquals(6.0, cfg.blockReachDistance, 1e-9);
    }

    @Test
    void loadReadsAllCurrentKeys(@TempDir Path tmp) throws IOException {
        Path p = tmp.resolve("cfg.properties");
        writeProps(p,
                "keepSprintEnabled=false\n" +
                "omniSprintEnabled=true\n" +
                "omniSprintMultiplier=1.12\n" +
                "fastPlaceEnabled=true\n" +
                "blockReachEnabled=true\n" +
                "blockReachDistance=6.5\n" +
                "timerEnabled=true\n" +
                "timerMultiplier=1.25\n");

        Config cfg = Config.load(p);

        assertFalse(cfg.keepSprintEnabled);
        assertTrue(cfg.omniSprintEnabled);
        assertEquals(1.12, cfg.omniSprintMultiplier, 1e-9);
        assertTrue(cfg.fastPlaceEnabled);
        assertTrue(cfg.blockReachEnabled);
        assertEquals(6.5, cfg.blockReachDistance, 1e-9);
        assertTrue(cfg.timerEnabled);
        assertEquals(1.25, cfg.timerMultiplier, 1e-9);
    }

    @Test
    void loadReadsLegacyTellyMomentumKeysWhenOmniAbsent(@TempDir Path tmp) throws IOException {
        Path p = tmp.resolve("legacy.properties");
        writeProps(p,
                "tellyMomentumEnabled=true\n" +
                "tellyMomentumMultiplier=1.08\n");

        Config cfg = Config.load(p);

        assertTrue(cfg.omniSprintEnabled);
        assertEquals(1.08, cfg.omniSprintMultiplier, 1e-9);
    }

    @Test
    void loadPrefersNewKeysOverLegacy(@TempDir Path tmp) throws IOException {
        Path p = tmp.resolve("both.properties");
        writeProps(p,
                "omniSprintEnabled=true\n" +
                "omniSprintMultiplier=1.11\n" +
                "tellyMomentumEnabled=false\n" +
                "tellyMomentumMultiplier=1.05\n");

        Config cfg = Config.load(p);

        assertTrue(cfg.omniSprintEnabled);
        assertEquals(1.11, cfg.omniSprintMultiplier, 1e-9);
    }

    @Test
    void loadGracefullyHandlesMalformedNumericValues(@TempDir Path tmp) throws IOException {
        Path p = tmp.resolve("bad.properties");
        writeProps(p,
                "omniSprintMultiplier=not-a-number\n" +
                "blockReachDistance=also-garbage\n" +
                "timerMultiplier=???\n");

        Config cfg = assertDoesNotThrow(() -> Config.load(p));

        assertEquals(1.0, cfg.omniSprintMultiplier, 1e-9);
        assertEquals(6.0, cfg.blockReachDistance, 1e-9);
        assertEquals(1.0, cfg.timerMultiplier, 1e-9);
    }

    @Test
    void loadToleratesMissingOptionalKeys(@TempDir Path tmp) throws IOException {
        Path p = tmp.resolve("minimal.properties");
        writeProps(p, "keepSprintEnabled=false\n");

        Config cfg = Config.load(p);

        assertFalse(cfg.keepSprintEnabled);
        assertFalse(cfg.omniSprintEnabled);
        assertEquals(1.0, cfg.omniSprintMultiplier, 1e-9);
        assertFalse(cfg.fastPlaceEnabled);
        assertFalse(cfg.blockReachEnabled);
        assertEquals(6.0, cfg.blockReachDistance, 1e-9);
        assertFalse(cfg.timerEnabled);
        assertEquals(1.0, cfg.timerMultiplier, 1e-9);
    }

    // --- Helpers ---

    private static void writeProps(Path p, String body) throws IOException {
        Files.createDirectories(p.getParent());
        Files.write(p, body.getBytes());
    }

    private static Properties readProps(Path p) throws IOException {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(p)) {
            props.load(in);
        }
        return props;
    }
}
