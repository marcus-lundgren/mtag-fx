package helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSystemHelper {
    private final static OS os = getOs();
    private static Path userDataPath = null;

    public Path GetUserDataPath() throws Exception {
        if (userDataPath != null) {
            return userDataPath;
        }

        Path basePath;
        if (isLinux()) {
            final var xdgDataHomeKey = "XDG_DATA_HOME";
            final var xdgDataHomeValue = System.getenv().getOrDefault(xdgDataHomeKey, null);

            // Fall back to the $HOME/.local/share folder
            if (xdgDataHomeValue == null) {
                // Add the home path
                basePath = Path.of(System.getenv("HOME"));
                basePath = basePath.resolve(".local").resolve("share");
            } else {
                // XDG_DATA_HOME is set. Honor it.
                basePath = Path.of(xdgDataHomeValue);
            }
        } else if (isWindows()) {
            basePath = Path.of(System.getenv("APPDATA"));
        } else {
            throw new Exception("Unsupported Operating System");
        }

        userDataPath = resolveAndCreateMtagPath(basePath);
        return userDataPath;
    }

    private Path resolveAndCreateMtagPath(Path basePath) throws IOException {
        final var mtagPath = basePath.resolve("mtag");
        return Files.exists(mtagPath) ? mtagPath : Files.createDirectories(mtagPath);
    }

    private boolean isLinux() {
        return os == OS.LINUX;
    }

    private boolean isWindows() {
        return os == OS.WINDOWS;
    }

    private static OS getOs() {
        final var osString = System.getProperty("os.name");

        // The parsing is inspired by PlatformUtil in JavaFX.
        if (osString.startsWith("Linux")) {
            return OS.LINUX;
        } else if (osString.startsWith("Windows")) {
            return OS.WINDOWS;
        }

        return OS.UNKNOWN;
    }

    private enum OS {
        UNKNOWN,
        LINUX,
        WINDOWS
    }
}
