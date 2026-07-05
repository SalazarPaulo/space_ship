package src.graphics;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/** Envoltorio de audio que permite desactivarlo globalmente desde Configuración. */
public final class Sound {
    private static final Set<Clip> KNOWN_CLIPS = ConcurrentHashMap.newKeySet();
    private static volatile boolean enabled = true;

    private final Clip clip;
    private final FloatControl volume;

    public Sound(Clip clip) {
        this.clip = clip;
        if (clip != null) KNOWN_CLIPS.add(clip);
        this.volume = clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)
                ? (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN)
                : null;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
        if (!value) {
            for (Clip knownClip : KNOWN_CLIPS) {
                try { knownClip.stop(); } catch (IllegalStateException ignored) { }
            }
        }
    }

    public static boolean isEnabled() { return enabled; }

    public void play() {
        if (!enabled || clip == null) return;
        try {
            clip.stop();
            clip.setFramePosition(0);
            clip.start();
        } catch (IllegalStateException ignored) { }
    }

    public void loop() {
        if (!enabled || clip == null) return;
        try {
            clip.stop();
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (IllegalStateException ignored) { }
    }

    public void stop() {
        if (clip == null) return;
        try { clip.stop(); } catch (IllegalStateException ignored) { }
    }

    public int getFramePosition() {
        try { return clip == null ? 0 : clip.getFramePosition(); }
        catch (IllegalStateException ignored) { return 0; }
    }

    public void changeVolume(float value) {
        if (volume == null) return;
        float clamped = Math.max(volume.getMinimum(), Math.min(volume.getMaximum(), value));
        volume.setValue(clamped);
    }
}
