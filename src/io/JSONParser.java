package src.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.gameObjects.Constants;

/** Persistencia JSON sin dependencias externas, compatible con registros previos. */
public final class JSONParser {
    private static final Path SCORE_FILE = Path.of(Constants.SCORE_PATH);
    private static final Pattern OBJECT = Pattern.compile("\\{([^{}]*)}");
    private static final Pattern SCORE = Pattern.compile("\\\"score\\\"\\s*:\\s*(-?\\d+)");
    private static final Pattern DATE = Pattern.compile("\\\"date\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"])*)\\\"");
    private static final Pattern NAME = Pattern.compile("\\\"name\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"])*)\\\"");

    private JSONParser() { }

    public static ArrayList<ScoreData> readFile() {
        ArrayList<ScoreData> scores = new ArrayList<>();
        if (!Files.isRegularFile(SCORE_FILE)) return scores;
        try {
            String json = Files.readString(SCORE_FILE, StandardCharsets.UTF_8);
            Matcher objects = OBJECT.matcher(json);
            while (objects.find()) {
                String object = objects.group(1);
                Matcher scoreMatcher = SCORE.matcher(object);
                Matcher dateMatcher = DATE.matcher(object);
                if (!scoreMatcher.find() || !dateMatcher.find()) continue;

                ScoreData data = new ScoreData();
                data.setScore(Integer.parseInt(scoreMatcher.group(1)));
                data.setDate(unescape(dateMatcher.group(1)));

                Matcher nameMatcher = NAME.matcher(object);
                if (nameMatcher.find()) data.setName(unescape(nameMatcher.group(1)));
                scores.add(data);
            }
        } catch (IOException | NumberFormatException ignored) {
            // Se muestran los registros válidos disponibles, o una tabla vacía si el archivo es corrupto.
        }
        return scores;
    }

    public static void writeFile(ArrayList<ScoreData> scores) throws IOException {
        Files.createDirectories(SCORE_FILE.getParent());
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < scores.size(); i++) {
            ScoreData data = scores.get(i);
            json.append("  {\"name\": \"").append(escape(data.getName()))
                    .append("\", \"score\": ").append(data.getScore())
                    .append(", \"date\": \"").append(escape(data.getDate())).append("\"}");
            if (i < scores.size() - 1) json.append(',');
            json.append('\n');
        }
        json.append("]\n");
        Files.writeString(SCORE_FILE, json.toString(), StandardCharsets.UTF_8);
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String unescape(String value) {
        return value.replace("\\\"", "\"").replace("\\\\", "\\");
    }
}
