package src.io;

import java.text.SimpleDateFormat;
import java.util.Date;

/** Registro persistente de una partida terminada. */
public class ScoreData {
    private static final String DEFAULT_NAME = "PILOTO";

    private String name;
    private String date;
    private int score;

    public ScoreData(int score, String name) {
        this.score = score;
        this.name = normalizeName(name);
        this.date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    /** Compatibilidad con el formato anterior, que no guardaba nombre. */
    public ScoreData(int score) {
        this(score, DEFAULT_NAME);
    }

    public ScoreData() {
        this.name = DEFAULT_NAME;
    }

    public String getName() { return normalizeName(name); }
    public void setName(String name) { this.name = normalizeName(name); }
    public String getDate() { return date == null ? "" : date; }
    public void setDate(String date) { this.date = date; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    private static String normalizeName(String value) {
        if (value == null) return DEFAULT_NAME;
        String cleaned = value.trim().replaceAll("\\s+", " ");
        if (cleaned.isEmpty()) return DEFAULT_NAME;
        return cleaned.length() > 16 ? cleaned.substring(0, 16) : cleaned;
    }
}
