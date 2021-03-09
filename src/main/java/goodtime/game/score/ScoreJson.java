package goodtime.game.score;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;

public class ScoreJson {

    private static final String CONFIG_PATH = "config/PlayerScore.json";

    private static final Gson GSON = new Gson();

    public HashMap<Long, Integer> scoreMap = new HashMap<>();

    public static Path getPath() {
        return Path.of(CONFIG_PATH);
    }

    private static ScoreJson INSTANCE;

    public static ScoreJson getInstance() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;

    }

    private ScoreJson() {
    }

    public static ScoreJson load() {

        Path config_Path = getPath();

        if (Files.exists(config_Path)) {
            try {
                return GSON.fromJson(Files.readString(config_Path), ScoreJson.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ScoreJson scoreJson = new ScoreJson();
            scoreJson.save();
            return scoreJson;
        }

        return null;

    }

    public void save() {
        try {
            Files.writeString(getPath(), GSON.toJson(this), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}