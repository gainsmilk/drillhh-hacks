package dev.gainsmilk.keepsprint.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Minimal properties-backed config. Lives at:
 *   ~/.weave/mods/keepsprint-config.properties
 *
 * Keys:
 *   keepSprintEnabled     (boolean, default true)
 *   omniSprintEnabled     (boolean, default false)
 *   omniSprintMultiplier  (double,  default 1.0, range 1.00-1.15)
 *   fastPlaceEnabled      (boolean, default false)
 *   blockReachEnabled     (boolean, default false)
 *   blockReachDistance    (double,  default 6.0, range 4.5-7.0)
 *
 * Back-compat: reads old tellyMomentumEnabled / tellyMomentumMultiplier
 * keys if omniSprint* keys are absent.
 */
public class Config {
    public boolean keepSprintEnabled = true;
    public boolean omniSprintEnabled = false;
    public double omniSprintMultiplier = 1.0;
    public boolean fastPlaceEnabled = false;
    public boolean blockReachEnabled = false;
    public double blockReachDistance = 6.0;
    public boolean timerEnabled = false;
    public double timerMultiplier = 1.0;

    // Keybinds (LWJGL keycodes). 0 = unbound. User edits the properties file
    // with the LWJGL integer for the desired key (e.g. 30 = A, 48 = B, ...).
    public int keepSprintKey = 0;
    public int omniSprintKey = 0;
    public int fastPlaceKey = 0;
    public int blockReachKey = 0;
    public int timerKey = 0;

    /** Parse an integer from a property value, returning a fallback on null/bad input. */
    private static int parseIntOr(String s, int fallback) {
        if (s == null) return fallback;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static Path configPath() {
        return Paths.get(System.getProperty("user.home"), ".weave", "mods", "keepsprint-config.properties");
    }

    public static Config load() {
        return load(configPath());
    }

    /** Test-friendly overload: load from an explicit path. Creates defaults if the file is missing. */
    public static Config load(Path path) {
        Config cfg = new Config();
        try {
            if (!Files.exists(path)) {
                if (path.getParent() != null) Files.createDirectories(path.getParent());
                cfg.save(path);
                return cfg;
            }
            Properties props = new Properties();
            try (InputStream in = Files.newInputStream(path)) {
                props.load(in);
            }
            String ks = props.getProperty("keepSprintEnabled");
            if (ks != null) cfg.keepSprintEnabled = Boolean.parseBoolean(ks.trim());

            String enabled = props.getProperty("omniSprintEnabled");
            if (enabled == null) enabled = props.getProperty("tellyMomentumEnabled"); // back-compat
            if (enabled != null) cfg.omniSprintEnabled = Boolean.parseBoolean(enabled.trim());

            String mult = props.getProperty("omniSprintMultiplier");
            if (mult == null) mult = props.getProperty("tellyMomentumMultiplier"); // back-compat
            if (mult != null) {
                try {
                    cfg.omniSprintMultiplier = Double.parseDouble(mult.trim());
                } catch (NumberFormatException ignored) { }
            }

            String fp = props.getProperty("fastPlaceEnabled");
            if (fp != null) cfg.fastPlaceEnabled = Boolean.parseBoolean(fp.trim());

            String br = props.getProperty("blockReachEnabled");
            if (br != null) cfg.blockReachEnabled = Boolean.parseBoolean(br.trim());

            String brd = props.getProperty("blockReachDistance");
            if (brd != null) {
                try {
                    cfg.blockReachDistance = Double.parseDouble(brd.trim());
                } catch (NumberFormatException ignored) { }
            }

            String te = props.getProperty("timerEnabled");
            if (te != null) cfg.timerEnabled = Boolean.parseBoolean(te.trim());

            String tm = props.getProperty("timerMultiplier");
            if (tm != null) {
                try {
                    cfg.timerMultiplier = Double.parseDouble(tm.trim());
                } catch (NumberFormatException ignored) { }
            }

            cfg.keepSprintKey = parseIntOr(props.getProperty("keepSprintKey"), 0);
            cfg.omniSprintKey = parseIntOr(props.getProperty("omniSprintKey"), 0);
            cfg.fastPlaceKey = parseIntOr(props.getProperty("fastPlaceKey"), 0);
            cfg.blockReachKey = parseIntOr(props.getProperty("blockReachKey"), 0);
            cfg.timerKey = parseIntOr(props.getProperty("timerKey"), 0);
        } catch (IOException e) {
            System.err.println("[TrenboloneBridgonate] failed to load config, using defaults: " + e.getMessage());
        }
        return cfg;
    }

    public void save() {
        save(configPath());
    }

    /** Test-friendly overload: save to an explicit path. */
    public void save(Path path) {
        Properties props = new Properties();
        props.setProperty("keepSprintEnabled", Boolean.toString(keepSprintEnabled));
        props.setProperty("omniSprintEnabled", Boolean.toString(omniSprintEnabled));
        props.setProperty("omniSprintMultiplier", Double.toString(omniSprintMultiplier));
        props.setProperty("fastPlaceEnabled", Boolean.toString(fastPlaceEnabled));
        props.setProperty("blockReachEnabled", Boolean.toString(blockReachEnabled));
        props.setProperty("blockReachDistance", Double.toString(blockReachDistance));
        props.setProperty("timerEnabled", Boolean.toString(timerEnabled));
        props.setProperty("timerMultiplier", Double.toString(timerMultiplier));
        props.setProperty("keepSprintKey", Integer.toString(keepSprintKey));
        props.setProperty("omniSprintKey", Integer.toString(omniSprintKey));
        props.setProperty("fastPlaceKey", Integer.toString(fastPlaceKey));
        props.setProperty("blockReachKey", Integer.toString(blockReachKey));
        props.setProperty("timerKey", Integer.toString(timerKey));
        try {
            if (path.getParent() != null) Files.createDirectories(path.getParent());
            try (OutputStream out = Files.newOutputStream(path)) {
                props.store(out, "Trenbolone Bridgonate config - singleplayer testing only");
            }
        } catch (IOException e) {
            System.err.println("[TrenboloneBridgonate] failed to save config: " + e.getMessage());
        }
    }
}
