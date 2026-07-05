package src.io;

import java.util.prefs.Preferences;

import src.graphics.Sound;

/** Preferencias persistentes de presentación y audio. */
public final class GameSettings {
    public enum ShipStyle {
        INTERCEPTOR("Interceptor azul"),
        SCOUT("Explorador verde"),
        FALCON("Falcón naranja"),
        VANGUARD("Vanguardia roja"),
        PHANTOM("Phantom interceptor");

        private final String label;
        ShipStyle(String label) { this.label = label; }
        public String label() { return label; }
    }

    public enum LaserStyle {
        BLUE("Azul"),
        GREEN("Verde"),
        RED("Rojo");

        private final String label;
        LaserStyle(String label) { this.label = label; }
        public String label() { return label; }
    }

    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(GameSettings.class);
    private static ShipStyle shipStyle = readShipStyle(PREFERENCES.get("shipStyle", ShipStyle.INTERCEPTOR.name()));
    private static LaserStyle laserStyle = enumValue(LaserStyle.class,
            PREFERENCES.get("laserStyle", LaserStyle.BLUE.name()), LaserStyle.BLUE);
    private static boolean soundEnabled = PREFERENCES.getBoolean("soundEnabled", true);

    private GameSettings() { }

    public static void load() { Sound.setEnabled(soundEnabled); }

    public static ShipStyle getShipStyle() { return shipStyle; }
    public static LaserStyle getLaserStyle() { return laserStyle; }
    public static boolean isSoundEnabled() { return soundEnabled; }

    public static void previousShip() { shipStyle = cycle(shipStyle, -1); save(); }
    public static void nextShip() { shipStyle = cycle(shipStyle, 1); save(); }
    public static void previousLaser() { laserStyle = cycle(laserStyle, -1); save(); }
    public static void nextLaser() { laserStyle = cycle(laserStyle, 1); save(); }

    public static void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
        Sound.setEnabled(enabled);
        save();
    }

    public static void toggleSound() { setSoundEnabled(!soundEnabled); }

    private static ShipStyle readShipStyle(String stored) {
        // Conserva las preferencias de las ediciones anteriores basadas en colores.
        if (stored == null) return ShipStyle.INTERCEPTOR;
        switch (stored) {
            case "BLUE": return ShipStyle.INTERCEPTOR;
            case "GREEN": return ShipStyle.SCOUT;
            case "ORANGE": return ShipStyle.FALCON;
            case "VIOLET":
            case "RED": return ShipStyle.VANGUARD;
            default: return enumValue(ShipStyle.class, stored, ShipStyle.INTERCEPTOR);
        }
    }

    private static <T extends Enum<T>> T cycle(T current, int direction) {
        T[] values = current.getDeclaringClass().getEnumConstants();
        int index = Math.floorMod(current.ordinal() + direction, values.length);
        return values[index];
    }

    private static void save() {
        try {
            PREFERENCES.put("shipStyle", shipStyle.name());
            PREFERENCES.put("laserStyle", laserStyle.name());
            PREFERENCES.putBoolean("soundEnabled", soundEnabled);
            PREFERENCES.flush();
        } catch (SecurityException | java.util.prefs.BackingStoreException ignored) {
            // El juego funciona aunque el perfil no permita persistir preferencias.
        }
    }

    private static <T extends Enum<T>> T enumValue(Class<T> type, String value, T fallback) {
        try {
            return Enum.valueOf(type, value);
        } catch (IllegalArgumentException exception) {
            return fallback;
        }
    }
}
