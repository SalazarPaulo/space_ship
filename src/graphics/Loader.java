package src.graphics;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class Loader {
    private Loader() { }

    /**
     * Busca primero dentro del classpath y después en resources/, res/ y la raíz
     * del proyecto. El fallback permite ejecutar el juego en IntelliJ sin tener
     * que marcar manualmente la carpeta resources como Resources Root.
     */
    public static URL findResource(String path) {
        URL classpathResource = Loader.class.getResource(path);
        if (classpathResource != null) return classpathResource;

        String clean = path.startsWith("/") ? path.substring(1) : path;
        List<Path> candidates = List.of(
                Path.of("resources", clean),
                Path.of("res", clean),
                Path.of(clean)
        );
        for (Path candidate : candidates) {
            if (Files.isRegularFile(candidate)) {
                try {
                    return candidate.toAbsolutePath().normalize().toUri().toURL();
                } catch (IOException ignored) {
                    // Se continúa con el siguiente candidato.
                }
            }
        }
        return null;
    }

    public static BufferedImage ImageLoader(String path) {
        URL resource = findResource(path);
        if (resource == null) {
            throw new IllegalStateException("No se encontró el recurso: " + path
                    + ". Verifica que exista en resources/.");
        }
        try {
            BufferedImage image = ImageIO.read(resource);
            if (image == null) throw new IOException("Formato de imagen no compatible");
            return image;
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo cargar la imagen: " + path, exception);
        }
    }

    public static Font loadFont(String path, int size) {
        URL resource = findResource(path);
        if (resource == null) return new Font(Font.SANS_SERIF, Font.PLAIN, size);
        try (var stream = resource.openStream()) {
            return Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(Font.PLAIN, (float) size);
        } catch (FontFormatException | IOException exception) {
            return new Font(Font.SANS_SERIF, Font.PLAIN, size);
        }
    }

    public static Clip loadSound(String path) {
        URL resource = findResource(path);
        if (resource == null) return null;
        try {
            Clip clip = AudioSystem.getClip();
            try (var stream = AudioSystem.getAudioInputStream(resource)) {
                clip.open(stream);
            }
            return clip;
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException | IllegalArgumentException exception) {
            // El juego sigue siendo jugable si el equipo no tiene un mezclador compatible.
            return null;
        }
    }
}
